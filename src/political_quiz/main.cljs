(ns political-quiz.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [chan <!]]
            [political-quiz.components :as comp]))

(enable-console-print!)

(defonce app-state (atom {:text      "Superrolig politikquiz!"
                          :step      0
                          :questions [{:question-text "Jag föredrar ketchup framför senap på min korv" :importance nil :answer nil}
                                      {:question-text "Äppelpaj är den godaste pajen" :importance nil :answer nil}
                                      {:question-text "Två personer kan få plats på en flytande dörr" :importance nil :answer nil}
                                      {:question-text "Episod I är den bästa Star Wars-filmen" :importance nil :answer nil}
                                      {:question-text "Vi borde använda mer Clojure(script)" :importance nil :answer nil}]}))

(defonce eventchannel (chan))

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
                                         (assoc q :answer value))))
                                   (:questions state))))

        :progress-click (if (= (:data event) "forward")
                          (swap! app-state update :step inc)
                          (swap! app-state update :step dec))

        :finish-click (swap! app-state assoc :step (count (:questions @app-state)))
        (println "Unknown event: " (:type event))))))

(defn app []
  (let [state @app-state]
    [:div
     [comp/canvas state eventchannel]]))

(reagent/render-component [app]
                          (. js/document (getElementById "app")))