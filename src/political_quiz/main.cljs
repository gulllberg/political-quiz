(ns political-quiz.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [chan <!]]
            [political-quiz.components :as comp]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello world!"
                          :step 0
                          :questions [{:question-text "hej1" :importance nil :answer nil}
                                      {:question-text "hej2" :importance nil :answer nil}
                                      {:question-text "hej3" :importance nil :answer nil}
                                      {:question-text "hej4" :importance nil :answer nil}
                                      {:question-text "hej5" :importance nil :answer nil}]}))

(defonce eventchannel (chan))

;; (swap! app-state assoc :checked {:name (:name (:data event)) :value (:value (:data event))})

(go
  (while true
    (let [event (<! eventchannel)]
      (condp = (:type event)
        :radio-click (let [state @app-state
                           name (:name (:data event))
                           value (:value (:data event))
                           question-text (:question-text (:data event))]
                       (swap! app-state assoc :questions
                              (map (fn [q]
                                     (if (not= (:question-text q) question-text)
                                       q
                                       (if (= name "importance")
                                         (assoc q :importance value)
                                         (assoc q :answer value)))) (:questions state))))
        :progress-click (if (= (:data event) "forward")
                          (swap! app-state update :step inc)
                          (swap! app-state update :step dec))
        (println "Unknown event")))))

(defn hello-world []
  (let [state @app-state]
    [:div
     [comp/questionnaire state eventchannel {:questions (map :question-text (:questions state))}]]))

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))
