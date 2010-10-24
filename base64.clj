(def to-base64 
  (zipmap 
    (range 0 63)
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"))
    
(def from-ascii 
  (zipmap 
   "!\"#$%&'()*+'-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
   (range 33 126)
))

(defn get-base64-first [arg]
  (let [str1-int (from-ascii (first arg))
        part1 (bit-shift-right str1-int 2)]
    (to-base64 part1)
  )
  )

(defn get-base64-second [arg]
  (let [str1-int (from-ascii (first arg))
        str2-int (from-ascii (first (rest arg)))
        part1 (bit-shift-left str1-int 4)
        part2 (bit-shift-right str2-int 4)]
    (to-base64 (bit-and 0x3F (bit-and part1 part2)))
  )
  )

(defn get-base64-third [arg]
  (let [str2-int (from-ascii (first (rest arg)))
        str3-int (from-ascii (first (rest (rest arg))))
        part1 (bit-shift-left str2-int 2)
        part2 (bit-shift-right str3-int 6)]
    (to-base64 (bit-and 0x3F (bit-and part1 part2)))
  )
  )

(defn get-base64-fourth [arg]
  (let [str3-int (from-ascii (first (rest (rest arg))))]
    (to-base64 (bit-and 0x3F str3-int))
  )
  )

(defn base64-encode [arg]
  (let [l (count arg)]

    (cond (= l 0) ""
          (= l 1) (str (get-base64-first arg) (get-base64-second arg))
          (= l 2) (str (get-base64-first arg) (get-base64-second arg))
          (= l 3) (str (get-base64-first arg) (get-base64-second arg) (get-base64-third arg))
    )
  )
  )
