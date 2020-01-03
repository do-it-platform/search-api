FROM confluentinc/cp-kafka-connect-base

ENV COMPONENT=kafka-connect

RUN echo "===> Installing elasticsearch connector..." \
    && apt-get -qq update \
    && apt-get install -y \
        confluent-kafka-connect-elasticsearch=${CONFLUENT_VERSION}${CONFLUENT_PLATFORM_LABEL}-${CONFLUENT_DEB_VERSION} \
    && echo "===> Cleaning up ..."  \
    && apt-get clean && rm -rf /tmp/* /var/lib/apt/lists/*

RUN echo "===> Installing Datagen Connector ..."
RUN confluent-hub install confluentinc/kafka-connect-datagen:latest --no-prompt