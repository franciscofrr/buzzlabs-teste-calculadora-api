(defproject buzzlabs-teste-calculadora-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [compojure "1.7.0"]
                 [http-kit "2.3.0"]
                 [ring/ring-defaults "0.3.4"]
                 [ring-cors "0.1.13"]
                 [com.datomic/local "1.0.291"]]
  :main ^:skip-aot buzzlabs-teste-calculadora-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
