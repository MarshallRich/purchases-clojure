(ns purchases-clojure.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.walk :as walk]))
(defn -main []
  (println "Enter 1 to search for furniture, 2 for Alcohol, 3 for Toiletries, 4 for Shoes, 5 for Food, and 6 for Jewelry.")
 
  (let [purchases (slurp "purchases.csv")
        text (read-line)
        purchases (str/split-lines purchases)
        purchases (map (fn [line] (str/split line #","))
                       purchases)
        header (first purchases)
        purchases (rest purchases)
        purchases (map (fn [line]
                         (apply hash-map (interleave header line)))
                       purchases)
                          
        purchases (walk/keywordize-keys purchases)
    
        category (cond 
                  (= text "1") "Furniture"
                  (= text "2") "Alcohol"
                  (= text "3") "Toiletries"
                  (= text "4") "Shoes"
                  (= text "5") "Food"
                  (= text "6") "Jewelry")
        purchases (filter (fn [line]
                           (= (:category line) category))
                    purchases)]
    (spit "filtered_purchases.edn" (pr-str purchases))
    purchases))
   
      
      
   
  
