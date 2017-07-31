(ns hemsida.components
  (:require [cljs.core.async :refer [put!]]))

(defn
  checked? [state {name :name option :option question-text :question-text}]
  (let [question (first (filter (fn [q]
                           (= (:question-text q) question-text)) (:questions state)))]
    (if (= name "importance")
      (= (str option) (:importance question))
      (= (str option) (:answer question)))))

(defn radio-group [state eventchannel {name :name options :options question-text :question-text}]
  [:div (map (fn [option]
               [:label {:key (str option)}
                [:input {:class-name "radio-input" :type "radio" :name name :value (str option)
                         :checked (checked? state {:name name :option option :question-text question-text})
                         :on-change (fn []
                                      (put! eventchannel {:type :radio-click :data {:name name :value (str option) :question-text question-text}}))}]
                [:span {:class-name "radio-label"} (str option)]]) options)])

(defn question [state eventchannel {question-text :question-text}]
  [:div
   [:div
    [:span "Hur viktig är denna fråga för dig? (1 = inte alls viktig, 6 = väldigt viktig)"]
    [radio-group state eventchannel {:name "importance" :options (range 1 7) :question-text question-text}]]
   [:div
    [:span "Hur väl håller du med om detta påstående? (1 = inte alls, 6 = mycket väl)"]
    [radio-group state eventchannel {:name "answer" :options (range 1 7) :question-text question-text}]]
   [:div question-text]])

(defn questionnaire [state eventchannel {questions :questions}]
  [:div
   [:h1 (:text state) (:step state)]
   [question state eventchannel {:question-text (nth questions (:step state))}]
   [:div
    [:button {:class-name "btn" :disabled (>= (:step state) (- (count questions) 1)) :on-click (fn []
                                                                                                 (put! eventchannel {:type :progress-click :data "forward"}))} "Framåt"]
    [:button {:class-name "btn" :disabled (<= (:step state) 0) :on-click (fn []
                                                                           (put! eventchannel {:type :progress-click :data "back"}))} "Bakåt"]]])
