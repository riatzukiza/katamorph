(ns katamorph.access.registry
  "Schemas for the access shapes, keyed by manifest kind.

   schema.cljs merges this map into the public registry so a namespace file
   and a JVM caller share one contract."
  (:require [katamorph.grant.shape :as grant]
            [katamorph.message.shape :as message]
            [katamorph.resource.shape :as resource]))

(def registry
  {:resource resource/ResourceContract
   :grant grant/GrantContract
   :revoke grant/RevokeContract
   :message message/MessageContract
   :mailbox message/MailboxContract
   :channel message/ChannelContract})
