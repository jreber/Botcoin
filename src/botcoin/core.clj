(ns botcoin.core
  (:gen-class))


;;;; Open Questions
(comment
  "1. How does Bitcoin handle bootstrapping new coins? What does a new coin look like?"
  "2. How does Bitcoin prevent node operators from running the presses all day long? What prevents node operators from creating blocks with nothing but new coins?")

;;;; begin "2. Transactions"
(def users {:alice {:name "Alice"
                    :symmetric-key "alice's key"}
            :bob {:name "Bob"
                  :symmetric-key "bob's key"}
            :charlie {:name "Charlie"
                      :symmetric-key "charlie's key"}
            :dave {:name "Dave"
                   :symmetric-key "dave's key"}})

(defn sign
  "Returns the signature of s using key."
  [key s]
  ;; I call this "symmetric key cryptography." Super secure.
  (str key ":" s))

(defn valid?
  "Returns true if sig is a valid signature of s using to key, false otherwise."
  [key sig s]
  (or (every? clojure.string/blank? [sig s])
      (= sig (str key ":" s))))

(defn transfer
  "Transfers funds from player payor to payee, returning an updated coin."
  [coin {payor-priv-key :symmetric-key payor-pub-key :symmetric-key}
   {payee-pub-key :symmetric-key}]
  (let [payor-signing-f (partial sign payor-priv-key)]
    (cons {:payee-pub-key payee-pub-key
           :signature (payor-signing-f
                       (str payee-pub-key "/" (first coin)))}
          coin)))

(def coin [])
(transfer coin (:alice users) (:bob users))

;;;; end "2. Transactions"

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
