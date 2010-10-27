(import '(org.apache.commons.codec.binary Base64))
(import '(org.apache.commons.lang RandomStringUtils))
;(use '[clojure.contrib.base64 :only (encode)])
;(use '[fencepost.base64 :only (base64-encode)])
(use '[clojure.contrib.base64])
(use '[fencepost.base64])

(def sample-size 10000)
(def string-size 64)

; Build up some sample data using commons-lang
(def sample-data (map #(RandomStringUtils/randomAlphabetic %) (repeat sample-size string-size)))
;(println sample-data)

; Instantiate a Base64 instance from commons-codec
(def codec-base64 (new Base64))

; Time each run
(time (map #(.encode codec-base64 %) sample-data))
(time (map #(clojure.contrib.base64/encode %) sample-data))
(time (map #(fencepost.base64/base64-encode %) sample-data))

