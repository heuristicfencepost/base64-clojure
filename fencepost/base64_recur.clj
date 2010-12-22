(ns fencepost.base64-recur)

; This could be placed within the defn below but rebuilding this map
; on each invocation seems a bit wasteful.
(def to-base64-recur
     (zipmap 
      (range 0 64)
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/")
     )

(defn base64-encode-recur [#^String arg]
  (loop [bytes (vec (.getBytes arg))
         rv ""]
    (let [samplesize (count bytes)]
      (cond
       (> samplesize 3)
       (let [[byte1 byte2 byte3] (take 3 bytes)
             char1 (bit-shift-right byte1 2)
             char2 (bit-and 0x3F (bit-or (bit-shift-left byte1 4) (bit-shift-right byte2 4)))
             char3 (bit-and 0x3F (bit-or (bit-shift-left byte2 2) (bit-shift-right byte3 6)))
             char4 (bit-and 0x3F byte3)]
         (recur (subvec bytes 3) (str rv (apply str (map to-base64-recur (list char1 char2 char3 char4)))))  
         )
       (= samplesize 3)
       (let [[byte1 byte2 byte3] (take 3 bytes)
             char1 (bit-shift-right byte1 2)
             char2 (bit-and 0x3F (bit-or (bit-shift-left byte1 4) (bit-shift-right byte2 4)))
             char3 (bit-and 0x3F (bit-or (bit-shift-left byte2 2) (bit-shift-right byte3 6)))
             char4 (bit-and 0x3F byte3)]
         (str rv (apply str (map to-base64-recur (list char1 char2 char3 char4))))
         )
       (= samplesize 2)
       (let [[byte1 byte2] (take 2 bytes)
             char1 (bit-shift-right byte1 2)
             char2 (bit-and 0x3F (bit-or (bit-shift-left byte1 4) (bit-shift-right byte2 4)))
             char3 (bit-and 0x3F (bit-shift-left byte2 2))]
         (str rv (apply str (map to-base64-recur (list char1 char2 char3))) "=")
         )
       (= samplesize 1)
       (let [byte1 (get bytes 0)
             char1 (bit-shift-right byte1 2)
             char2 (bit-and 0x3F (bit-shift-left byte1 4))]
         (str rv (apply str (map to-base64-recur (list char1 char2))) "==")
         )
       (= samplesize 0) ""
       )
      )
    )
  )

