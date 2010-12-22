(ns fencepost.test)

(import '(org.apache.commons.codec.binary Base64))
(import '(org.apache.commons.lang RandomStringUtils))
(use '[clojure.contrib.base64])
(use '[fencepost.base64_letfn])
(use '[fencepost.base64-recur])

(def sample-size 100)
(def max-string-size 256)

; Build up some sample data using commons-lang.  Sample data is built
; before any tests are run; this allows us to apply uniform test data
; to each implementation and to avoid corrupting timings with the
; generation of random data.
(def sample-data (map #(RandomStringUtils/randomAscii (* % (rand-int max-string-size))) (repeat sample-size 1)))

; Instantiate a Base64 instance from commons-codec
(def codec-base64 (new Base64 -1))

; Time each run.  Mapping a function onto our sample data produces a
; lazy sequence so we have to take the additional step of realizing
; the sequence; thus the conversion to a vector.
(println "Commons-codec")
(def commons-codec-data (time (vec (map #(new String (.encode codec-base64 (.getBytes %))) sample-data))))
(println (get commons-codec-data 0))

(println "clojure-contrib")
(def clojure-contrib-data (time (vec (map #(clojure.contrib.base64/encode-str %) sample-data))))
(println (get clojure-contrib-data 0))

; Simple sanity check; take commons-codec output as canonical and
; verify that what our implementations generate match up with the
; canonical sample.
(assert (.equals(get commons-codec-data 0) (get clojure-contrib-data 0)))

(println "fencepost/base64_letfn")
(def fencepost-data (time (vec (map #(fencepost.base64_letfn/base64-encode-ascii %) sample-data))))
(println (get fencepost-data 0))
(assert (.equals (get commons-codec-data 0) (get fencepost-data 0)))

(println "fencepost-recur")
(def fencepost-recur-data (time (vec (map #(fencepost.base64-recur/base64-encode-recur %) sample-data))))
(println (get fencepost-recur-data 0))
(assert (.equals (get commons-codec-data 0) (get fencepost-recur-data 0)))
