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
  (let [weight (:weight (nth opinion-list index))
        total-weight (reduce (fn [sum opinion]
                               (+ sum (:weight opinion)))
                             0
                             opinion-list)]
    (/ weight total-weight)))

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
                (* 2 (+ (/ 6 17) (/ 5 16))))
           (is= (calculate-question-error {:user-opinion-list  user-opinion-test-list
                                           :party-opinion-list party-opinion-test-list
                                           :index              4})
                (* 2 (+ (/ 1 17) (/ 2 16)))))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list index :index}]
  (let [user-answer (:answer (nth user-opinion-list index))
        party-answer (:answer (nth party-opinion-list index))
        user-relative-weight (calculate-relative-weight user-opinion-list index)
        party-relative-weight (calculate-relative-weight party-opinion-list index)]
    (* (Math/abs (- user-answer party-answer))
       (+ user-relative-weight party-relative-weight))))

;; Should have 3 versions, this is model 1
(defn- calculate-total-error
  "Calculates the total error given two opinion-lists"
  {:test (fn []
           (is= (calculate-total-error {:user-opinion-list  user-opinion-test-list
                                        :party-opinion-list party-opinion-test-list})
                (+ 0
                   (* 3 (+ (/ 5 17) (/ 3 16)))
                   (* 2 (+ (/ 6 17) (/ 5 16)))
                   (* 2 (+ (/ 2 17) (/ 3 16)))
                   (* 2 (+ (/ 1 17) (/ 2 16))))))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list}]
  (let [number-of-questions (count user-opinion-list)]
    (reduce (fn [sum index]
              (+ sum (calculate-question-error {:user-opinion-list  user-opinion-list
                                                :party-opinion-list party-opinion-list
                                                :index              index})))
            0
            (range number-of-questions))))

(defn calculate-user-party-agreement
  "Takes all answers from a user and a party and calculates their agreement in %"
  {:test (fn []
           (is= (calculate-user-party-agreement {:user-opinion-list  user-opinion-test-list
                                                 :party-opinion-list party-opinion-test-list})
                (- 100 (* 10 (+ 0
                                (* 3 (+ (/ 5 17) (/ 3 16)))
                                (* 2 (+ (/ 6 17) (/ 5 16)))
                                (* 2 (+ (/ 2 17) (/ 3 16)))
                                (* 2 (+ (/ 1 17) (/ 2 16)))))))
           (error? (calculate-user-party-agreement {:user-opinion-list  (conj user-opinion-test-list {:answer 1 :weight 1})
                                                    :party-opinion-list party-opinion-test-list})))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list}]
  {:pre (= (count user-opinion-list) (count party-opinion-list))}
  (- 100
     (* 10 (calculate-total-error {:user-opinion-list  user-opinion-list
                                   :party-opinion-list party-opinion-list}))))



(comment (float (calculate-user-party-agreement {:user-opinion-list  user-opinion-test-list
                                                 :party-opinion-list party-opinion-test-list})))