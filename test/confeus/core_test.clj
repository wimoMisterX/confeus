(ns confeus.core-test
  (:require [clojure.test :refer :all]
            [confeus.core :as c]))

(deftest test-job-config 
  (testing "merge job config"
    (is (= (c/update-config :merge 
                            {{:environment "uat" :implementation "wow"} ["10.21.2.20" "10.21.2.80"]}
                            {:targets ["10.21.2.80" "10.21.2.140"]
                             :labels {:environment "uat" :implementation "wow"}})
           {{:environment "uat" :implementation "wow"} ["10.21.2.140" "10.21.2.20" "10.21.2.80"]})))

  (testing "set job config"
    (is (= (c/update-config :set
                            {{:environment "uat" :implementation "wow"} ["10.21.2.20" "10.21.2.80"]}
                            {:targets ["10.21.2.140"]
                             :labels {:environment "uat" :implementation "wow"}})
           {{:environment "uat" :implementation "wow"} ["10.21.2.140"]}))))
