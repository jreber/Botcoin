(ns botcoin.core-test
  (:require [clojure.test :refer :all]
            [botcoin.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(deftest sign-test
  (testing "Signing a string"
    (is (= (sign "my key" "string to sign")
           "my key:string to sign"))))

(deftest valid?-test
  (testing "Verifying signatures"
    (is (valid? "my key" "my key:string to sign" ":string to sign"))
    (is (not (valid? "my key" "my key:bogus string" "string to sign")))
    (is (not (valid? "my key" "not my key:string to sign" "string to sign")))))

(deftest transfer-test
  (testing "Transfering a new coin"
    (is (= [{:payee-pub-key "bob key"
             :signature "alice key:bob key/"}]
           (transfer [] "bob key" (partial sign "alice key")))))
  (testing "Transferring an existing coin"
    (let [users {:alice {:name "Alice"
                         :symmetric-key "alice's key"}
                 :bob {:name "Bob"
                       :symmetric-key "bob's key"}
                 :charlie {:name "Charlie"
                           :symmetric-key "charlie's key"}}
          first-tran {:payee-pub-key "bob key"
                      :signature "alice key:2029465041"}]
      (is (= [{:payee-pub-key "charlie key",
               :signature "bob key:charlie key/{:payee-pub-key \"bob key\", :signature \"alice key:2029465041\"}"}
              {:payee-pub-key "bob key", :signature "alice key:2029465041"}]
             (transfer [first-tran] (:bob users) (:charlie users)))))))
