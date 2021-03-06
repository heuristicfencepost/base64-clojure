(ns fencepost.base64-recur)

; This could be placed within the defn below but rebuilding this map
; on each invocation seems a bit wasteful.
(def to-base64-recur
     (zipmap 
      (range 0 64)
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/")
     )

(def from-base64-recur
     (zipmap 
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
      (range 0 64)
      )
     )

(defn base64-encode-recur [#^String arg]
  "Encode the input string into the base64 alphabet.  Currently uses the platform charset for the conversion
   of the input string to bytes; this could be generalized if need be."
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

(defn base64-decode-recur [#^String arg]
  "Decode the string of base64-encoded content into a String"
  (loop [currstr arg
         rv ""]
    (let [currstrsize (count currstr)]
      (cond
       ; String longer than 4 characters
       (> currstrsize 4)
       (let [[char1 char2 char3 char4] (map from-base64-recur (take 4 currstr)) 
             byte1 (bit-or (bit-shift-left char1 2) (bit-shift-right char2 4))
             byte2 (bit-and 0xff (bit-or (bit-shift-left char2 4) (bit-shift-right char3 2)))
             byte3 (bit-and 0xff (bit-or (bit-shift-left char3 6) char4))
             newstr (String. (byte-array (map byte [byte1 byte2 byte3])))]
         (recur (.substring currstr 4) (str rv newstr))
         )
       (= currstrsize 0) ""
       (< currstrsize 4)
       (throw (IllegalArgumentException. "Malformed base64 string; must have even multiple of four characters"))
       (identity 1)
       (let [curratom (apply str (take 4 currstr))]
         (cond
          ; Third byte is the bottom two bits of byte 2 plus all six low
          ; bits of byte 3
          (re-matches #"[A-Za-z0-9+/]{4}" curratom)
          (let [[char1 char2 char3 char4] (map from-base64-recur (take 4 currstr))
                byte1 (bit-or (bit-shift-left char1 2) (bit-shift-right char2 4))
                byte2 (bit-and 0xff (bit-or (bit-shift-left char2 4) (bit-shift-right char3 2)))
                byte3 (bit-and 0xff (bit-or (bit-shift-left char3 6) char4))
                newstr (String. (byte-array (map byte [byte1 byte2 byte3])))]
            (str rv newstr)
            )
          ; Second byte is the low four bits of byte 2 plus the top four
          ; bits of byte 3
          (re-matches #"[A-Za-z0-9+/]{3}=" curratom)
          (let [[char1 char2 char3] (map from-base64-recur (take 3 currstr))
                byte1 (bit-or (bit-shift-left char1 2) (bit-shift-right char2 4))
                byte2 (bit-and 0xff (bit-or (bit-shift-left char2 4) (bit-shift-right char3 2)))
                newstr (String. (byte-array (map byte [byte1 byte2])))]
            (str rv newstr)
            )
          ; First byte is the low six bits of byte 1 plus the top two
          ; bits of byte 2
          (re-matches #"[A-Za-z0-9+/]{2}==" curratom)
          (let [[char1 char2] (map from-base64-recur (take 2 currstr))
                byte1 (bit-or (bit-shift-left char1 2) (bit-shift-right char2 4))
                newstr (String. (byte-array [(byte byte1)]))]
            (str rv newstr)
            )
          (identity 1)
          (throw (IllegalArgumentException. "Malformed base64 string"))
          )
         )
       )
      )
    )
  )

