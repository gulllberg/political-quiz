(ns political-quiz.core
  (:require
    [ysera.test :refer [is= error?]]))

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

(defn- calculate-question-error-1
  "Calculates question error given two opinion-lists and a question index using method 1"
  {:test (fn []
           (is= (calculate-question-error-1 {:user-opinion-list  user-opinion-test-list
                                             :party-opinion-list party-opinion-test-list
                                             :index              0})
                0)
           (is= (calculate-question-error-1 {:user-opinion-list  user-opinion-test-list
                                             :party-opinion-list party-opinion-test-list
                                             :index              2})
                (* 2 (+ (/ 6 17) (/ 5 16))))
           (is= (calculate-question-error-1 {:user-opinion-list  user-opinion-test-list
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

(defn- calculate-question-error-2
  "Calculates question error given two opinion-lists and a question index using method 2"
  {:test (fn []
           (is= (calculate-question-error-2 {:user-opinion-list  user-opinion-test-list
                                             :party-opinion-list party-opinion-test-list
                                             :index              0})
                0)
           (is= (calculate-question-error-2 {:user-opinion-list  user-opinion-test-list
                                             :party-opinion-list party-opinion-test-list
                                             :index              1})
                (* 3 2 (+ (/ 1 17) (/ 2 16))))
           (is= (calculate-question-error-2 {:user-opinion-list  user-opinion-test-list
                                             :party-opinion-list party-opinion-test-list
                                             :index              4})
                (* 2 1 (+ (/ 5 17) (/ 3 16)))))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list index :index}])

(defn- calculate-question-error-3
  "Calculates question error given two opinion-lists and a question index using method 2"
  {:test (fn []
           (is= (calculate-question-error-3 {:user-opinion-list  user-opinion-test-list
                                             :party-opinion-list party-opinion-test-list
                                             :index              0})
                0)
           (is= (calculate-question-error-3 {:user-opinion-list  user-opinion-test-list
                                             :party-opinion-list party-opinion-test-list
                                             :index              1})
                (* 3 2))
           (is= (calculate-question-error-3 {:user-opinion-list  user-opinion-test-list
                                             :party-opinion-list party-opinion-test-list
                                             :index              4})
                (* 2 1)))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list index :index}])

(defmulti calculate-question-error (fn [{method :method}]
                                     method))

(defmethod calculate-question-error :1 [args]
  (calculate-question-error-1 args))

(defmethod calculate-question-error :2 [args]
  (calculate-question-error-2 args))

(defmethod calculate-question-error :3 [args]
  (calculate-question-error-3 args))

(defn- calculate-total-error
  "Calculates the total error given two opinion-lists"
  {:test (fn []
           (is= (calculate-total-error {:user-opinion-list  user-opinion-test-list
                                        :party-opinion-list party-opinion-test-list
                                        :method :1})
                (+ 0
                   (* 3 (+ (/ 5 17) (/ 3 16)))
                   (* 2 (+ (/ 6 17) (/ 5 16)))
                   (* 2 (+ (/ 2 17) (/ 3 16)))
                   (* 2 (+ (/ 1 17) (/ 2 16))))))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list :as args}]
  (let [number-of-questions (count user-opinion-list)]
    (reduce (fn [sum index]
              (+ sum (calculate-question-error (merge args {:index index}))))
            0
            (range number-of-questions))))

(defn- calculate-user-party-agreement-1
  "Takes all answers from a user and a party and calculates their agreement in % using method 1"
  {:test (fn []
           (is= (calculate-user-party-agreement-1 {:user-opinion-list  user-opinion-test-list
                                                 :party-opinion-list party-opinion-test-list
                                                 :method             :1})
                (- 100 (* 10 (+ 0
                                (* 3 (+ (/ 5 17) (/ 3 16)))
                                (* 2 (+ (/ 6 17) (/ 5 16)))
                                (* 2 (+ (/ 2 17) (/ 3 16)))
                                (* 2 (+ (/ 1 17) (/ 2 16)))))))
           (error? (calculate-user-party-agreement-1 {:user-opinion-list  (conj user-opinion-test-list {:answer 1 :weight 1})
                                                    :party-opinion-list party-opinion-test-list
                                                    :method             :1})))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list :as args}]
  {:pre (= (count user-opinion-list) (count party-opinion-list))}
  (- 100
     (* 10 (calculate-total-error args))))

(defn- calculate-user-party-agreement-2
  "Takes all answers from a user and a party and calculates their agreement in % using method 2"
  {:test (fn []
           (is= (calculate-user-party-agreement-2 {:user-opinion-list  user-opinion-test-list
                                                   :party-opinion-list party-opinion-test-list
                                                   :method             :2})
                (- 100 (/ (* 4 (+ 0
                                  (* 3 2)
                                  (* 2 1)
                                  (* 2 1)
                                  (* 2 1)))
                          5)))
           (error? (calculate-user-party-agreement-2 {:user-opinion-list  (conj user-opinion-test-list {:answer 1 :weight 1})
                                                      :party-opinion-list party-opinion-test-list
                                                      :method             :2})))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list :as args}])

(defn- calculate-user-party-agreement-3
  "Takes all answers from a user and a party and calculates their agreement in % using method 3"
  {:test (fn []
           (is= (calculate-user-party-agreement-3 {:user-opinion-list  user-opinion-test-list
                                                   :party-opinion-list party-opinion-test-list
                                                   :method             :3})
                (- 100 (* 2 (+ 0
                                (* 3 2 (+ (/ 5 17) (/ 3 16)))
                                (* 2 1 (+ (/ 6 17) (/ 5 16)))
                                (* 2 1 (+ (/ 2 17) (/ 3 16)))
                                (* 2 1 (+ (/ 1 17) (/ 2 16)))))))
           (error? (calculate-user-party-agreement-3 {:user-opinion-list  (conj user-opinion-test-list {:answer 1 :weight 1})
                                                      :party-opinion-list party-opinion-test-list
                                                      :method             :3})))}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list :as args}])

(defmulti calculate-user-party-agreement (fn [{method :method}]
                                     method))

(defmethod calculate-user-party-agreement :1 [args]
  (calculate-user-party-agreement-1 args))

(defmethod calculate-user-party-agreement :2 [args]
  (calculate-user-party-agreement-2 args))

(defmethod calculate-user-party-agreement :3 [args]
  (calculate-user-party-agreement-3 args))