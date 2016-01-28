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
    (is (valid? []))
    (is (valid? [{:payee-pub-key "first recipient"
                  :signature "foobar"}]))
    (is (valid? [{:payee-pub-key "second recipient",
                  :signature "first recipient:332913733"}
                 {:payee-pub-key "first recipient",
                  :signature "foobar"}]))
    (is (not (valid? "my key" "my key:bogus string" "string to sign")))
    (is (not (valid? "my key" "not my key:string to sign" "string to sign")))))

(deftest transfer-test
  (testing "Transfering a new coin"
    
    (is (= [{:payee-pub-key "new owner key",
             :signature "previous owner key:-1422918588"}]
           (transfer [] {:symmetric-key "previous owner key"}
                     {:symmetric-key "new owner key"}))))
  (testing "Transferring an existing coin"
    (is (= [{:payee-pub-key "second recipient",
             :signature "first recipient:332913733"}
            {:payee-pub-key "first recipient",
             :signature "foobar"}]
         (transfer [{:payee-pub-key "first recipient"
                     :signature "foobar"}]
                   {:symmetric-key "first recipient"}
                   {:symmetric-key "second recipient"})))))
