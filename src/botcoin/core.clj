(ns botcoin.core
  (:gen-class))


;;;; Open Questions
(comment
  "1. How does Bitcoin handle bootstrapping new coins? What does a new coin look like?
       -> I resolved it by allowing the first transaction to contain whatever sig
          it wants. Unfortunately, this allows nodes to create coin unchecked.
   2. How does Bitcoin prevent node operators from running the presses all day
      long? What prevents node operators from creating blocks with nothing but
      new coins?")

;;;; end Open Questions

;;;; start misc utils
(defn chunked-pmap
  "Partitions s into n chunks and then uses pmap to process each chunk."
  [n f s]
  (map f s))
;;;; end misc utils

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
  "Returns the signature of o using key."
  [key o]
  ;; I call this "symmetric key cryptography." Super secure.
  (str key ":" (hash o)))

(defn valid?
  "Returns true if sig is a valid signature of o using to key, false otherwise."
  ([coin]
   (or (<= (count coin) 1)
       (and (valid? (:payee-pub-key (second coin))
                    (:signature (first coin))
                    (assoc (second coin) :new-payee-pub-key (:payee-pub-key (first coin))))
            (valid? (rest coin)))))
  ([key sig o]
   (= sig (str key ":" (hash o)))))

(defn transfer
  "Transfers funds from player payor to payee, returning an updated coin."
  [coin {payor-priv-key :symmetric-key payor-pub-key :symmetric-key}
   {payee-pub-key :symmetric-key}]
  (if (valid? coin)
    (let [payor-signing-f (partial sign payor-priv-key)]
      (cons {:payee-pub-key payee-pub-key
             :signature (payor-signing-f
                         (assoc (first coin) :new-payee-pub-key payee-pub-key))}
            coin))
    (throw (Exception. (str "Invalid coin: " coin)))))

(def coin [])
(transfer coin (:alice users) (:bob users))

;;;; end "2. Transactions"

;;;; begin "3. Timestamp Server" and "4. Proof-of-Work"
(defn create-block
  "Collects transactions into a block. The first element of the resulting sequence
   is the nonce, the second is the hash of the previous block, and all subsequent
   values are the transactions in the block."
  [target-prefix prev-hash & trans]
  ; find a nonce that will make the hash be our magic number
  (let [block (cons prev-hash trans)]
    (cons (first
           (filter #(.startsWith (str (hash (cons % block)))
                                 (str target-prefix))
                   (range Long/MAX_VALUE)))
          block)))

(create-block 1234 0xDEADBEEF 1 2 3 4 5)
;;;; end "3. Timestamp Server" and "4. Proof-of-Work"

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
