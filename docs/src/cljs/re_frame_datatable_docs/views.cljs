(ns re-frame-datatable-docs.views
  (:require [re-frame-datatable.core :as dt]
            [re-frame-datatable-docs.subs :as subs]
            [cljs.pprint :as pp]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))


(defn sneak-peek-for-readme []
  [dt/datatable
   :songs
   [::subs/songs-list]
   [{::dt/column-key   [:index]
     ::dt/sorting      {::dt/enabled? true}
     ::dt/th-classes   ["two" "wide"]
     ::dt/column-label "#"}
    {::dt/column-key   [:name]
     ::dt/th-classes   ["ten" "wide"]
     ::dt/column-label "Name"}
    {::dt/column-key   [:duration]
     ::dt/column-label "Duration"
     ::dt/sorting      {::dt/enabled? true}
     ::dt/th-classes   ["four" "wide"]
     :render-fn        (fn [val]
                         [:span
                          (let [m (quot val 60)
                                s (mod val 60)]
                            (if (zero? m)
                              s
                              (str m ":" (when (< s 10) 0) s)))])}]

   {::dt/pagination    {::dt/enabled? true
                        ::dt/per-page 5}
    ::dt/table-classes ["ui" "table" "celled"]}])



(defn formatted-code [data]
  [:small
   [:pre
    [:code {:class "clojure"}
     (with-out-str (pp/pprint data))]]])



(defn tabs-wrapper [dt-id subscription columns-def options]
  (let [data (re-frame/subscribe subscription)
        example-dom-id (str (name dt-id) "-example")
        source-dom-id (str (name dt-id) "-source")
        data-dom-id (str (name dt-id) "-data")]

    (fn []
      (let [dt-def [dt/datatable dt-id subscription columns-def options]]
        [:div
         [:div.ui.top.attached.tabular.menu
          [:a.active.item
           {:data-tab example-dom-id} "Example"]
          [:a.item
           {:data-tab source-dom-id} "Source"]
          [:a.item
           {:data-tab data-dom-id} "Data"]]

         [:div.ui.bottom.attached.active.tab.segment
          {:data-tab example-dom-id}
          dt-def]

         [:div.ui.bottom.attached.tab.segment
          {:data-tab source-dom-id}
          [formatted-code
           (vec (cons ::re-frame-datatable.core/datatable
                      (->> dt-def (rest) (filter (complement nil?)))))]]

         [:div.ui.bottom.attached.tab.segment
          {:data-tab data-dom-id}
          [formatted-code @data]]]))))


(defn main-panel []
  (reagent/create-class
    {:component-function
     (fn []
       [:div
        [:div.ui.main.text.container
         [:h1.ui.header
          {:style {:margin-bottom "2em"
                   :margin-top    "1em"}}
          "re-frame-datatable"
          [:div.sub.header "DataTable component for re-frame 0.8.0+"]]

         [:div.ui.section
          [:h3.ui.dividing.header ""]
          [tabs-wrapper
           :basic-usage
           [::subs/songs-list]
           [{::dt/column-key   [:index]
             ::dt/column-label "#"}
            {::dt/column-key   [:name]
             ::dt/column-label "Name"}]]]

         [:div.ui.section
          [:h3.ui.dividing.header "Basic usage"]
          [tabs-wrapper
           :basic-usage
           [::subs/songs-list]
           [{::dt/column-key   [:index]
             ::dt/column-label "#"}
            {::dt/column-key   [:name]
             ::dt/column-label "Name"}]]]]])


     :component-did-mount
     (fn []
       (.tab (js/$ ".menu .item")))}))