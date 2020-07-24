(ns beacon-server.handler
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))

(defn- find-match [{:keys [playerName]}]
  (println "Finding match for" playerName)
  {:id 1
   :players [playerName]})

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (POST "/api/find-match" {:keys [body]} (resp/response (find-match body)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
     (wrap-defaults api-defaults)
     (wrap-json-response)
     (wrap-json-body {:keywords? true})
     (wrap-cors :access-control-allow-origin [#".*"]
                :access-control-allow-methods [:get :post :options])))
