(ns political-quiz.core
  (:require
    [ysera.test :refer [is= error?]]))

;; Opinion-list = List({:weight W :answer A})

(def user-opinion-test-list
  [{:answer 3 :weight 3}
   {:answer 1 :weight 5}
   {:answer 6 :weight 6}
   {:answer 3 :weight 2}
   {:answer 3 :weight 1}])

(def party-opinion-test-list
  [{:answer 3 :weight 3}
   {:answer 4 :weight 3}
   {:answer 4 :weight 5}
   {:answer 1 :weight 3}
   {:answer 5 :weight 2}])

(defn- calculate-relative-weight
  "Calculates the relative weight for a question given an opinion-list"
  {:test (fn []
           (is= (calculate-relative-weight user-opinion-test-list 0)
                (/ 3 17))
           (is= (calculate-relative-weight user-opinion-test-list 2)
                (/ 6 17))
           (is= (calculate-relative-weight party-opinion-test-list 4)
                (/ 2 16)))}
  [opinion-list index]
  ;; Needs nothing
  )

;; Should have 3 versions, this is model 1
(defn- calculate-question-error
  "Calculates question error given two opinion-lists and a question index"
  {:test (fn []
           (is= (calculate-question-error {:user-opinion-list  user-opinion-test-list
                                           :party-opinion-list party-opinion-test-list
                                           :index              0})
                0)
           (is= (calculate-question-error {:user-opinion-list  user-opinion-test-list
                                           :party-opinion-list party-opinion-test-list
                                           :index              2})
                (* 2 (/ 6 17) (/ 5 16)))
           (is= (calculate-question-error {:user-opinion-list  user-opinion-test-list
                                           :party-opinion-list party-opinion-test-list
                                           :index              4})
                (* 2 (/ 1 17) (/ 2 16))))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list index :index}]
  ;; Function potentially needing answers, weights and relative weights (depending on model)
  )

;; Should have 3 versions, this is model 1
(defn- calculate-total-error
  "Calculates the total error given two opinion-lists"
  {:test (fn []
           (is= (calculate-total-error {:user-opinion-list  user-opinion-test-list
                                        :party-opinion-list party-opinion-test-list})
                (+ 0
                   (* 3 (/ 5 17) (/ 3 16))
                   (* 2 (/ 6 17) (/ 5 16))
                   (* 2 (/ 2 17) (/ 3 16))
                   (* 2 (/ 1 17) (/ 2 16)))))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list}]
  ;; Sum of all question errors
  )

(defn calculate-user-party-agreement
  "Takes all answers from a user and a party and calculates their agreement in %"
  {:test (fn []
           (is= (calculate-user-party-agreement {:user-opinion-list  user-opinion-test-list
                                                 :party-opinion-list party-opinion-test-list})
                (- 100 (* 10 (+ 0
                                (* 3 (/ 5 17) (/ 3 16))
                                (* 2 (/ 6 17) (/ 5 16))
                                (* 2 (/ 2 17) (/ 3 16))
                                (* 2 (/ 1 17) (/ 2 16))))))
           (error? (calculate-user-party-agreement {:user-opinion-list  (conj user-opinion-test-list {:answer 1 :weight 1})
                                                    :party-opinion-list party-opinion-test-list})))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list}]
  ;; PRE: Lists have same length
  ;; Function of total error
  )

