(ns purchases-clojure.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [compojure.core :as c]
            [hiccup.core :as h]
            [ring.adapter.jetty :as j]
            [ring.middleware.params :as p]))
(defn read-purchases []
;  (println "Enter 1 to search for furniture, 2 for Alcohol, 3 for Toiletries, 4 for Shoes, 5 for Food, and 6 for Jewelry.")
 
  (let [purchases (slurp "purchases.csv")
;        text (read-line)
        purchases (str/split-lines purchases)
        purchases (map (fn [line] (str/split line #","))
                       purchases)
        header (first purchases)
        purchases (rest purchases)
        purchases (map (fn [line]
                         (apply hash-map (interleave header line)))
                       purchases)
                          
        purchases (walk/keywordize-keys purchases)]
    
;        category (cond 
;                  (= text "1") "Furniture"
;                  (= text "2") "Alcohol"
;                  (= text "3") "Toiletries"
;                  (= text "4") "Shoes"
;                  (= text "5") "Food"
;                  (= text "6") "Jewelry")
;        purchases (filter (fn [line]
;                           (= (:category line) category))
;                    purchases)]
;    (spit "filtered_purchases.edn" (pr-str purchases))
    purchases))

(defn categories-html [purchases]
  (let [all-categories (map :category purchases)
        unique-categories (set all-categories)
        sorted-categories (sort unique-categories)]
     [:div
      (map (fn [category]
             [:span
               [:a {:href (str "/?category=" category)} category]
               " "])
        sorted-categories)]))

(defn purchases-html [purchases]
 [:ol
    (map (fn [purchase]    
          [:li (str 
                 "Customer ID#: "(:customer_id purchase) ", "
                 "Date: " (:date purchase) ", "
                 "Category: " (:category purchase) ", " 
                 "Credit Card#: " (:credit_card purchase) ", "
                 "CVV#: " (:cvv purchase))]) 
         purchases)]) 
   
(c/defroutes app
 (c/GET "/" request
  (let [params (:params request)
        category (get params "category")
        category (or category "Furniture")
        purchases (read-purchases)
        filtered-purchases (filter (fn [purchase]
                                     (= (:category purchase) category))
                             purchases)]
    (h/html [:html 
               [:body
                (categories-html purchases)
                (purchases-html filtered-purchases)]]))))

(defn -main []
  (j/run-jetty (p/wrap-params app) {:port 3000})) 
      
   
  
