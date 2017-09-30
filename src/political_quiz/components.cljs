(ns political-quiz.components
  (:require [cljs.core.async :refer [put!]]
            [political-quiz.party-opinion-lists :as party-opinion-lists]
            [political-quiz.core :as core]))

(defn- checked?
  [state {name :name option :option question-text :question-text}]
  (let [question (first (filter (fn [q]
                                  (= (:question-text q) question-text)) (:questions state)))]
    (if (= name "importance")
      (= (str option) (:importance question))
      (= (str option) (:answer question)))))

(defn- question-answered?
  [state question-text]
  (let [question (->> state
                      (:questions)
                      (filter (fn [q]
                                (= (:question-text q)
                                   question-text)))
                      (first))]
    (and (not (nil? (:answer question)))
         (not (nil? (:importance question))))))

(defn- all-questions-answered?
  [state]
  (reduce (fn [a q]
            (and a (question-answered? state (:question-text q))))
          true
          (:questions state)))

(defn radio-group
  [state eventchannel {name :name options :options question-text :question-text}]
  [:div (map (fn [option]
               [:label {:key (str option)}
                [:input {:class-name "radio-input" :type "radio" :name name :value (str option)
                         :checked    (checked? state {:name name :option option :question-text question-text})
                         :on-change  (fn []
                                       (put! eventchannel {:type :radio-click :data {:name name :value (str option) :question-text question-text}}))}]
                [:span {:class-name "radio-label"} (str option)]]) options)])

(defn question
  [state eventchannel {question-text :question-text}]
  [:div
   [:h3 (str question-text " (Fråga " (inc (:step state)) " av " (count (:questions state)) ")")]
   [:div
    [:span "Hur viktig är denna fråga för dig? (1 = inte alls viktig, 6 = väldigt viktig)"]
    [radio-group state eventchannel {:name "importance" :options (range 1 7) :question-text question-text}]]
   [:div
    [:span "Hur väl håller du med om detta påstående? (1 = inte alls, 6 = mycket väl)"]
    [radio-group state eventchannel {:name "answer" :options (range 1 7) :question-text question-text}]]])

(defn questionnaire
  [state eventchannel {questions :questions}]
  (let [step (:step state)]
    [:div
     [:h1 (:text state)]
     [question state eventchannel {:question-text (nth questions step)}]
     [:div
      [:button {:class-name "btn"
                :disabled   (or (>= step (dec (count questions)))
                                (not (question-answered? state (nth questions step))))
                :on-click   (fn []
                              (put! eventchannel {:type :progress-click :data "forward"}))}
       "Framåt"]
      [:button {:class-name "btn"
                :disabled   (<= step 0)
                :on-click   (fn []
                              (put! eventchannel {:type :progress-click :data "back"}))}
       "Bakåt"]
      [:button {:class-name "btn"
                :disabled   (not (all-questions-answered? state))
                :on-click   (fn []
                              (put! eventchannel {:type :finish-click}))}
       "Skicka in svar"]]]))

(defn party-result
  [{user-opinion-list :user-opinion-list party :party}]
  [:div
   [:h3 (:name party)]
   [:div (str "Sympatisering: " (float (core/calculate-user-party-agreement {:user-opinion-list user-opinion-list :party-opinion-list (:opinion-list party)})) " %")]
   [:div (str "Dom svarade så här: " (:opinion-list party))]])

(defn results-overview
  [state]
  (let [party-list [party-opinion-lists/party1
                    party-opinion-lists/party2
                    party-opinion-lists/party3
                    party-opinion-lists/party4
                    party-opinion-lists/party5]
        user-opinion-list (->> state
                               (:questions)
                               (map (fn [q]
                                      {:weight (js/parseInt (:importance q))
                                       :answer (js/parseInt (:answer q))})))]
    [:div
     [:h3 "Du svarade så här"]
     [:div (str user-opinion-list)]
     (for [party party-list]
       ^{:key party} [party-result {:user-opinion-list user-opinion-list :party party}])]))

(defn canvas
  [state eventchannel]
  (if (not= (:step state) (count (:questions state)))
    [questionnaire state eventchannel {:questions (map :question-text (:questions state))}]
    [results-overview state]))
