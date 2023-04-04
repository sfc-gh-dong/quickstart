# quickstart
Snowpark Hands On Quickstart

This repo is a quick bootstrap of the Snowpark Lab https://quickstarts.snowflake.com/guide/getting_started_snowpark_machine_learning/index.html

The Dockerfile uses the jupyter minimal installation and builds Snowpark, required Python libraries. openssl on top of that. It'll call openssl to 
generate the RSA key for key pair authentication with Snowpark

Conda_env.yml contains all the required Python libraries, you can add/remove them as required.

Detailed steps here :

1. Sign up for Snowflake
2. Install Docker
3. git clone https://github.com/sfc-gh-dong/quickstart.git
4. docker build -t snowpark-notebook:latest ./
5. docker run -it -p 10000:8888 -e GRANT_SUDO=yes --user root snowpark-notebook:latest
6. Modify config.py (you should see this after you open Jupyter notebook)
7. ALTER USER <your_user> SET RSA_PUBLIC_KEY='MIIBIjANBgkqhki…' (run this in Snowflake)

