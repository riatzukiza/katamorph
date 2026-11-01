(ns katamorph.message.law
  "Mailbox projection and the channel-to-resource arrow.

   mailbox is the fold of messages addressed to one Did. It is a pure
   projection: delivery is an append the consumer performs."
  (:require [katamorph.message.shape :as shape]
            [katamorph.resource.law :as resource]
            [katamorph.schema.core :as schema]))

(def registry
  {:message shape/MessageContract
   :mailbox shape/MailboxContract
   :channel shape/ChannelContract})

(defn explain-message
  [message]
  (schema/validate registry :message message))

(defn explain-mailbox
  [mailbox]
  (schema/validate registry :mailbox mailbox))

(defn explain-channel
  [channel]
  (schema/validate registry :channel channel))

(defn mailbox
  "Messages addressed to did, in log order."
  [did messages]
  (reduce (fn [acc message]
            (if (= did (:msg/to message))
              (conj acc message)
              acc))
          []
          messages))

(defn channel-resource
  "A channel is a resource of kind :msg/channel."
  [channel]
  (cond-> {:resource/id (:channel/id channel)
           :resource/kind :msg/channel
           :resource/owner (:channel/owner channel)}
    (some? (:channel/store channel))
    (assoc :resource/store (:channel/store channel))

    (some? (:channel/location channel))
    (assoc :resource/location (:channel/location channel))

    (some? (:channel/metadata channel))
    (assoc :resource/metadata (:channel/metadata channel))))

(defn channel-admitted?
  [channel]
  (and (:ok (explain-channel channel))
       (resource/admitted? (channel-resource channel))))
