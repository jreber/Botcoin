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
(defn create-valid-coin
  [] ())

(defn create-invalid-coin
  [] ())

(defn create-valid-blockchain
  [] ())
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

(defn new?
  "Returns true if the given coin is new, false otherwise."
  [coin]
  (<= (count coin) 1))

(new? [])
(new? [{:bah "humbug"}])

(defn valid?
  "Returns true if sig is a valid signature of o using to key, false otherwise."
  ([coin]
   (or (new? coin)
       (and (valid? (:payee-pub-key (second coin))
                    (:signature (first coin))
                    (assoc (second coin)
                           :new-payee-pub-key (:payee-pub-key (first coin))))
            (valid? (rest coin)))))
  ([key sig o]
   (= sig (str key ":" (hash o)))))

(defn transfer
  "Transfers funds from player payor to payee, returning an updated coin.

   This is from the payor's point of view, so the payee must distrust the
   result of this function."
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
  [target-prefix prev-hash & coins]
  ; find a nonce that will make the hash be our magic number
  (when-let [trans (seq (map first (filter valid? coins)))]
    (let [block (cons prev-hash trans)] (cons (first
                   (filter #(.startsWith (str (hash (cons % block)))
                                         (str target-prefix))
                           (range Long/MAX_VALUE)))
                  block))))

(create-block 1234 "prev hash"
              [{:payee-pub-key "alice",
                 :signature "zero:332913733"}]
              [{:payee-pub-key "charlie",
                 :signature "hector:332913733"}])

(defn new-blockchain?
  [chain]
  (<= (count chain) 1))

(defn valid-block?
  "Confirms that the most recent block is valid."
  [target-prefix chain]
  (or (new-blockchain? chain)
      (let [block (first chain)
            prev-chain (rest chain)]
        (and (.startsWith (str (hash block))
                          (str target-prefix))
             (= (second block)
                (hash (first prev-chain)))
             (every? valid? (rest (rest block)))))))


(defn valid-blockchain?
  "Confirms that the entire blockchain is valid."
  [chain]
  ())


(defn valid-tran?
  "Confirms, for a payee, that the most recent transaction is valid given a
   previous record of blocks."
  [chain coin]
  ; to be valid, a coin must (1) be internally consistent (see valid?)
  ; and (2) the most recent transaction must appear in an accepted block.
  (or (new? coin)
      (and (valid? coin)
           (contains? (:payee-pub-key (second coin))
                      (mapcat #(map :payee-pub-key %)
                              chain)))))

;;;; end "3. Timestamp Server" and "4. Proof-of-Work"

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
