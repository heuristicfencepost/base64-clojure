(use '[fencepost.base64-recur])

(assert (= (base64-encode-recur "") ""))
(assert (= (base64-encode-recur "f") "Zg=="))
(assert (= (base64-encode-recur "fo") "Zm8="))
(assert (= (base64-encode-recur "foo") "Zm9v"))
(assert (= (base64-encode-recur "foob") "Zm9vYg=="))
(assert (= (base64-encode-recur "fooba") "Zm9vYmE="))
(assert (= (base64-encode-recur "foobar") "Zm9vYmFy"))

(assert (= (base64-decode-recur "") ""))
(assert (= (base64-decode-recur "Zg==") "f"))
(assert (= (base64-decode-recur "Zm8=") "fo"))
(assert (= (base64-decode-recur "Zm9v") "foo"))
(assert (= (base64-decode-recur "Zm9vYg==") "foob"))
(assert (= (base64-decode-recur "Zm9vYmE=") "fooba"))
(assert (= (base64-decode-recur "Zm9vYmFy") "foobar"))
