(ns fencepost.base64_letfn)

; This could be placed within the defn below but rebuilding this map
; on each invocation seems a bit wasteful.
(def to-base64 
     (zipmap 
      (range 0 64)
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/")
     )

(defn base64-encode
  "Encode input string into base64 alphabet.  String should be encoded
  in the character set specified by encoding."
  [#^String arg #^String encoding]
  (letfn [
      (do-one-byte [somebyte]
                   {:curr [(bit-shift-right somebyte 2)] :remainder (bit-and 0x3F (bit-shift-left somebyte 4)) }
                   )
      (do-two-bytes [somebytes]
                    (let [[byteone bytetwo] somebytes
                          {curr :curr remainder :remainder} (do-one-byte byteone)]
                      {:curr (conj curr (bit-or remainder (bit-shift-right bytetwo 4))) :remainder (bit-and 0x3F (bit-shift-left bytetwo 2))}
                      )
                    )
      (do-three-bytes [somebytes]
                      (let [[byteone bytetwo bytethree] somebytes
                            {curr :curr remainder :remainder} (do-two-bytes [byteone bytetwo])]
                        (conj (conj curr (bit-or remainder (bit-shift-right bytethree 6))) (bit-and 0x3F bytethree))
                        )
                      )
      (somefn [somechars]
                (let [l (count somechars)]
                  (cond
                   (= l 3)
                   (apply str (map #(to-base64 %) (do-three-bytes somechars)))
                   (= l 2)
                   (let [{curr :curr remainder :remainder} (do-two-bytes somechars)]
                     (str (apply str (map #(to-base64 %) curr)) (to-base64 remainder) "=")
                     )
                   (= l 1)
                   (let [{curr :curr remainder :remainder} (do-one-byte (first somechars))]
                     (str (apply str (map #(to-base64 %) curr)) (to-base64 remainder) "==")
                     ))))]
    (apply str (map somefn (partition 3 3 () (.getBytes arg encoding))))))

(defn base64-encode-ascii
  "Encode input string into base64 alphabet.  Input string is assumed
  to be encoded in UTF-8.  If the input is encoded with a different
  character set base64-encode should be used instead of this method."
  [#^String arg]
  (base64-encode arg "UTF8")
  )
