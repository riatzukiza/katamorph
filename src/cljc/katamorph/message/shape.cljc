(ns katamorph.message.shape
  "Message, channel, and mailbox shapes.

   A message is a clio event addressed to a Did. Identity follows
   clio.law.event: :event/id, :event/stream, :event/seq, :event/causes.
   The envelope keys :event/type, :event/actor, :event/at, and :event/body
   are carried so a later clio envelope can read them. :event/hash is an
   optional sha256 hex string; Katamorph does not compute it.

   A channel is a :msg/channel resource. A mailbox names the Did whose
   message fold it is. The fold itself is katamorph.message.law/mailbox."
  (:require [clojure.string :as str]
            [katamorph.edn.portable :as portable]
            [katamorph.resource.shape :as resource]
            [katamorph.schema.condition :as condition]))

(def uuid-pattern
  #"(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")

(defn uuid-string?
  [value]
  (boolean (and (string? value) (re-matches uuid-pattern value))))

(defn event-seq?
  [value]
  (and (condition/portable-integer? value)
       (pos? value)))

(defn event-identity-valid?
  "clio.law.event identity, restricted to portable seqs so canonical-form accepts them."
  [event]
  (let [event-id (:event/id event)
        causes (:event/causes event)]
    (and (uuid-string? event-id)
         (string? (:event/stream event))
         (not (str/blank? (:event/stream event)))
         (event-seq? (:event/seq event))
         (vector? causes)
         (every? uuid-string? causes)
         (= (count causes) (count (set causes)))
         (not (some #{event-id} causes)))))

(defn sha256-hex?
  [value]
  (boolean (and (string? value) (re-matches #"[0-9a-f]{64}" value))))

(defn addressed?
  [message]
  (and (event-identity-valid? message)
       (resource/did? (:msg/to message))
       (resource/did? (:event/actor message))
       (keyword? (:event/type message))
       (portable/portable-instant? (:event/at message))
       (map? (:event/body message))
       (portable/portable-edn? (:event/body message))))

(def MessageContract
  [:and
   [:map {:closed false}
    [:message/id [:fn {:error/message "message id must be a non-blank string or keyword"}
                  resource/resource-id?]]
    [:event/id [:fn {:error/message "event id must be a UUID string"} uuid-string?]]
    [:event/stream :string]
    [:event/seq [:fn {:error/message "event seq must be a positive portable integer"}
                 event-seq?]]
    [:event/causes [:vector [:fn {:error/message "a cause must be a UUID string"}
                             uuid-string?]]]
    [:event/type :keyword]
    [:event/actor resource/Did]
    [:event/at portable/PortableEdn]
    [:event/body [:map {:closed false}]]
    [:event/hash {:optional true}
     [:fn {:error/message "event hash must be a lowercase sha256 hex string"}
      sha256-hex?]]
    [:msg/to resource/Did]
    [:msg/channel {:optional true}
     [:fn {:error/message "channel id must be a non-blank string or keyword"}
      resource/resource-id?]]
    [:contract/kind {:optional true} [:= :message]]
    [:contract/id {:optional true} [:or :string :keyword]]]
   [:fn {:error/message "a message is a clio event addressed to a did"}
    addressed?]
   [:fn {:error/message "a message must be portable EDN"}
    portable/portable-edn?]])

(def MailboxContract
  [:and
   [:map {:closed false}
    [:mailbox/id [:fn {:error/message "mailbox id must be a non-blank string or keyword"}
                  resource/resource-id?]]
    [:mailbox/did resource/Did]
    [:mailbox/channel {:optional true}
     [:fn {:error/message "channel id must be a non-blank string or keyword"}
      resource/resource-id?]]
    [:contract/kind {:optional true} [:= :mailbox]]
    [:contract/id {:optional true} [:or :string :keyword]]]
   [:fn {:error/message "a mailbox must be portable EDN"}
    portable/portable-edn?]])

(defn channel-located?
  [channel]
  (let [store (:channel/store channel)
        location (:channel/location channel)]
    (or (keyword? store)
        (and (string? store) (not (str/blank? store)))
        (and (string? location) (not (str/blank? location)))
        (and (map? location) (seq location)))))

(def ChannelContract
  [:and
   [:map {:closed false}
    [:channel/id [:fn {:error/message "channel id must be a non-blank string or keyword"}
                  resource/resource-id?]]
    [:channel/owner resource/Did]
    [:channel/kind {:optional true} [:= :msg/channel]]
    [:channel/store {:optional true} [:or :keyword :string]]
    [:channel/location {:optional true} portable/PortableEdn]
    [:channel/metadata {:optional true}
     [:map-of [:or :keyword :string] portable/PortableEdn]]
    [:contract/kind {:optional true} [:= :channel]]
    [:contract/id {:optional true} [:or :string :keyword]]]
   [:fn {:error/message "a channel names :channel/store or :channel/location"}
    channel-located?]
   [:fn {:error/message "a channel must be portable EDN"}
    portable/portable-edn?]])
