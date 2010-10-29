; Verify correctness of a specific base64-encode implementation.  Run
; this script after loading any concrete implementation.

(assert (= (base64-encode "") ""))
(assert (= (base64-encode "f") "Zg=="))
(assert (= (base64-encode "fo") "Zm8="))
(assert (= (base64-encode "foo") "Zm9v"))
(assert (= (base64-encode "foob") "Zm9vYg=="))
(assert (= (base64-encode "fooba") "Zm9vYmE="))
(assert (= (base64-encode "foobar") "Zm9vYmFy"))
