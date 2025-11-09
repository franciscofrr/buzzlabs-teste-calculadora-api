(ns buzzlabs-teste-calculadora-api.utils)

(defn calculadora [operatorLeft operationSign operatorRight]
  (case operationSign
    "+" (+ (Integer/parseInt operatorLeft) (Integer/parseInt operatorRight))
    "-" (- (Integer/parseInt operatorLeft) (Integer/parseInt operatorRight))
    "*" (* (Integer/parseInt operatorLeft) (Integer/parseInt operatorRight))
    "/" (/ (Integer/parseInt operatorLeft) (Integer/parseInt operatorRight))))
 