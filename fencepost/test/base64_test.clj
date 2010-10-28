(ns fencepost.test)

(import '(org.apache.commons.codec.binary Base64))
(import '(org.apache.commons.lang RandomStringUtils))
(use '[clojure.contrib.base64])
(use '[fencepost.base64])

(def sample-size 100)
(def string-size 256)

; Build up some sample data using commons-lang
(def sample-data (map #(RandomStringUtils/randomAlphabetic %) (repeat sample-size string-size)))
;(println sample-data)

; Instantiate a Base64 instance from commons-codec
(def codec-base64 (new Base64))

; Time each run
(println "Commons-codec")
(time (assert (= (count (map #(.encode codec-base64 (.getBytes %)) sample-data)) sample-size))) 
(println "clojure-contrib")
(time (assert (= (count (map #(clojure.contrib.base64/encode-str %) sample-data)) sample-size))) 
(println "fencepost")
(time (assert (= (count (map #(fencepost.base64/base64-encode %) sample-data)) sample-size)))

