package com.es2.vadebicicleta.externo.commons.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger {}

@Configuration
class MailConfig {
    /*@Bean
    fun configMailReceiver() : Ima {
        val imapMailReceiver = ImapMailReceiver(url())
            imapMailReceiver.setShouldDeleteMessages(deleteOnRead)
            imapMailReceiver.isShouldMarkMessagesAsRead = markAsRead

        return imapMailReceiver
    }

    *//*@Bean
    fun mainIntegration(props: EmailProperties): IntegrationFlow {
        return IntegrationFlow
            .from(
                Mail.imapInboundAdapter(props.url())
                    .shouldDeleteMessages(false)
                    .simpleContent(true)
                    .autoCloseFolder(false)
                    .javaMailProperties {p: PropertiesBuilder ->
                        p.put(
                            "mail.imap.ssl.enable",
                            "true"
                        )}
            ) {
                e: SourcePollingChannelAdapterSpec ->
                e.poller(Pollers.fixedDelay(60000)) // This is still required but will not trigger unless the channel is used
            }
            .channel(MessageChannels.queue())
            .handle { message: Message<*> ->
                logger.info { "New message received: $message" }
            }
            .get()
    }

    *//*@Bean
    fun queueChannel(): QueueChannel {
        return QueueChannel()
    }*/
}
