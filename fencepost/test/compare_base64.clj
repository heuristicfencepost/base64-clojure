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

(println "Encoding")

; Time each run.  Mapping a function onto our sample data produces a
; lazy sequence so we have to take the additional step of realizing
; the sequence; thus the conversion to a vector.
(println "Commons-codec")
(def commons-codec-data (time (doall (map #(String. (.encode codec-base64 (.getBytes %))) sample-data))))
(println (first commons-codec-data))

(println "clojure-contrib")
(def clojure-contrib-data (time (doall (map #(clojure.contrib.base64/encode-str %) sample-data))))
(println (first clojure-contrib-data))

; Simple sanity check; take commons-codec output as canonical and
; verify that what our implementations generate match up with the
; canonical sample.
(assert (.equals (first commons-codec-data) (first clojure-contrib-data)))

(println "fencepost/base64_letfn")
(def fencepost-data (time (doall (map #(fencepost.base64_letfn/base64-encode-ascii %) sample-data))))
(println (first fencepost-data))
(assert (.equals (first commons-codec-data) (first fencepost-data)))

(println "fencepost-recur")
(def fencepost-recur-data (time (doall (map #(fencepost.base64-recur/base64-encode-recur %) sample-data))))
(println (first fencepost-recur-data))
(assert (.equals (first commons-codec-data) (first fencepost-recur-data)))

(println "Decoding")

(println "Commons-codec")
(def commons-codec-data2 (time (doall (map #(String. (.decode codec-base64 (.getBytes %))) commons-codec-data))))
(println (first commons-codec-data2))
(assert (.equals (first sample-data) (first commons-codec-data2)))

(println "clojure-contrib")
(def clojure-contrib-data2 (time (doall (map #(clojure.contrib.base64/decode-str %) clojure-contrib-data))))
(println (first clojure-contrib-data2))
(assert (.equals (first commons-codec-data2) (first clojure-contrib-data2)))

(println "fencepost-recur")
(def fencepost-recur-data2 (time (doall (map #(fencepost.base64-recur/base64-decode-recur %) fencepost-recur-data))))
(println (first fencepost-recur-data2))
(assert (.equals (first commons-codec-data2) (first fencepost-recur-data2)))
