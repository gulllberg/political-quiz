(ns political-quiz.core
  (:require
    [ysera.test :refer [is is= is-not error?]]))

;; Opinion-list = List({:weight W :answer A})

(defn- calculate-relative-weight
  "Calculates the relative weight for a question given an opinion-list"
  {:test (fn [])}
  [opinion-list index]
  ;; Needs nothing
  )

;; Should have 3 versions
(defn- calculate-question-error
  "Calculates question error given two opinion-lists and a question index"
  {:test (fn [])}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list index :index}]
  ;; Function potentially needing answers, weights and relative weights (depending on model)
  )

;; Should have 3 versions
(defn- calculate-total-error
  "Calculates the total error given two opinion-lists"
  {:test (fn [])}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list}]
  ;; Sum of all question errors
  )

(defn calculate-user-party-agreement
  "Takes all answers from a user and a party and calculates their agreement in %"
  {:test (fn [])}
  [{user-opinion-list :user-opinion-list party-opinion-list :party-opinion-list}]
  ;; PRE: Lists have same length
  ;; Function of total error
  )

