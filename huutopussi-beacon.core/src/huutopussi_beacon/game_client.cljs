(ns ^:figwheel-hooks huutopussi-beacon.game-client
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<! timeout]]))

(defonce dev-mode? (= "http://localhost:9500/" (str (-> js/window .-location))))

(println "Dev mode?" dev-mode?)

(defonce api-url (if dev-mode?
                   "http://localhost:3000/api"
                   "/api"))

(defn wait-until-state [match-id expected-state]
  (go-loop []
           (<! (timeout 500))
           (let [url (str api-url "/match/" match-id)
                 {:keys [body status] :as response} (<! (http/get url {:with-credentials? false}))]
             (if (= 200 status)
               (if (= expected-state (:status body))
                 body
                 (recur))
               (throw (js/Error. (str "Match find failed with response: " response)))))))

(defn call-find-match [player-name]
  (go (let [url (str api-url "/match")
            response (<! (http/post url
                                    {:json-params {:playerName player-name}
                                     :with-credentials? false}))]
        (if (= 200 (:status response))
          (:body response)
          (throw (js/Error. (str "Call to url " url " failed with response: " response)))))))

(defn get-cards [id player-name]
  (go (let [url (str api-url "/match/" id "/cards/" player-name)
            response (<! (http/get url {:with-credentials? false}))]
        (if (= 200 (:status response))
          (:body response)
          (throw (js/Error. (str "Call to url " url " failed with response: " response)))))))

(defn call-start-game [id]
  (go (let [response (<! (http/post (str api-url "/match/" id "/start" )
                                    {:json-params {}
                                     :with-credentials? false}))]
        (if (= 200 (:status response))
          (:body response)
          (throw (js/Error. (str "Match find failed with response: " response)))))))

(defn play-card [id player index]
  (println "Playing card: " index)
  (go (let [url (str api-url "/match/" id "/play/" player "/card/" index)
            response (<! (http/put url {:with-credentials? false}))]
        (if (= 200 (:status response))
          (:body response)
          (throw (js/Error. (str "Call to url " url " failed with response: " response)))))))