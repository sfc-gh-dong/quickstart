FROM jupyter/minimal-notebook:latest

WORKDIR /home/jovyan/snowpark
COPY conda_env.yml .
COPY config.py .

USER root
RUN apt-get -y install openssl
USER jovyan
RUN openssl genrsa 2048 | openssl pkcs8 -topk8 -inform PEM -out rsa_key.p8 -passout pass:snowflake && openssl rsa -in rsa_key.p8 -pubout -out rsa_key.pub -passin pass:snowflake
RUN conda env create -f conda_env.yml
SHELL ["conda", "run", "-n", "snowpark", "/bin/bash", "-c"]
RUN pip install jupyter ipython && \
ipython kernel install --name "snowpark" --user
