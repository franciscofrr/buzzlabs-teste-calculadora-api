(ns buzzlabs-teste-calculadora-api.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.cors :refer [wrap-cors]]
            [buzzlabs-teste-calculadora-api.utils :refer [calculadora]]
            [clojure.string :as str]
            [datomic.client.api :as datomic])

  (:gen-class))

; ----------------------------------------------------------
; Database

(def calc-schema [{:db/ident :calc/id
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc "The id of the operation"}
                  {:db/ident :calc/operation
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc "The operation"}
                  {:db/ident :calc/result
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc "The result of the operation"}])

(def client (datomic/client {:server-type :datomic-local :system "dev"}))
(datomic/create-database client {:db-name "calcs"})
(def conn (datomic/connect client {:db-name "calcs"}))
(datomic/transact conn {:tx-data calc-schema})

; ----------------------------------------------------------
; Functions

(def id-counter (atom 0))

(defn record-calculation [operation result]
  (swap! id-counter inc)
  (let [calculation [{:calc/id (str @id-counter)
                      :calc/operation operation
                      :calc/result (str result)}]]
    (datomic/transact conn {:tx-data calculation}))
  (str result))

(defn getHistory []
  (let [db (datomic/db conn)]
    (datomic/q '[:find ?id ?operation ?result
                 :keys id operation result
                 :where
                 [?calc :calc/id ?id]
                 [?calc :calc/operation ?operation]
                 [?calc :calc/result ?result]]
               db)))

; ----------------------------------------------------------
; Endpoints

(defn history-handler [req]
  {:status  200
   :body    (getHistory)})

(defn calculate-handler [req]
  {:status  200
   :body    (-> (let [operatorLeft (-> req :params :operatorLeft)
                      operationSign (-> req :params :operationSign)
                      operatorRight (-> req :params :operatorRight)]
                  (record-calculation (str/join [operatorLeft operationSign operatorRight]) (calculadora operatorLeft operationSign operatorRight))))})

; ----------------------------------------------------------
; Routes

(defroutes app-routes
  (GET "/history" [] history-handler)
  (POST "/calculate" [] calculate-handler))

; ----------------------------------------------------------
; Main

(defn -main
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "4000"))]
    (server/run-server
     (wrap-cors
      (-> app-routes
          (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false)))
      :access-control-allow-origin [#"http://localhost:3000"]
      :access-control-allow-methods [:post :get]
      :access-control-allow-headers #{"accept" "accept-encoding"
                                      "accept-language" "authorization"
                                      "content-type" "origin"})
     {:port port})
    (println (str "Webserver started at http:/127.0.0.1:" port "/"))))