(def to-base64 
  (zipmap 
    (range 0 63)
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"))
    
(def from-ascii 
  (zipmap 
   "!\"#$%&'()*+'-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
   (range 33 126)
   ))

(defn do-one-byte [somebyte]
  {:sofar [(bit-shift-right somebyte 2)] :candidate (bit-and 0x3F (bit-shift-left somebyte 4)) }
  )

(defn do-two-bytes [somebytes]
  (let [[byteone bytetwo] somebytes
        {sofar :sofar candidate :candidate} (do-one-byte byteone)]
    {:sofar (conj sofar (bit-or candidate (bit-shift-right bytetwo 4))) :candidate (bit-and 0x3F (bit-shift-left bytetwo 2))}
    )
  )

(defn do-three-bytes [somebytes]
  (let [[byteone bytetwo bytethree] somebytes
        {sofar :sofar candidate :candidate} (do-two-bytes [byteone bytetwo])]
    (conj (conj sofar (bit-or candidate (bit-shift-right bytethree 6))) (bit-and 0x3F bytethree))
    )
  )

(defn base64-encode [arg]
  (letfn [
      (somefn [somebytes]
                (let [someints (vec (map #(from-ascii %) somebytes)) l (count someints)]
                  (cond
                   (= l 3)
                   (apply str (map #(to-base64 %) (do-three-bytes someints)))
                   (= l 2)
                   (let [{sofar :sofar candidate :candidate} (do-two-bytes someints)]
                     (str (apply str (map #(to-base64 %) sofar)) (to-base64 candidate) "=")
                     )
                   (= l 1)
                   (let [{sofar :sofar candidate :candidate} (do-one-byte (first someints))]
                     (str (apply str (map #(to-base64 %) sofar)) (to-base64 candidate) "==")
                     ))))]
    (apply str (map somefn (partition 3 3 () arg)))))
