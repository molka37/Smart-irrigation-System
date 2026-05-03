FROM jupyter/scipy-notebook

RUN pip install joblib
RUN pip install xgboost
RUN pip install websockets
RUN pip install paho-mqtt



USER root

RUN mkdir model raw_data processed_data results


ENV RAW_DATA_DIR=/home/jovyan/raw_data
ENV PROCESSED_DATA_DIR=/home/jovyan/processed_data
ENV MODEL_DIR=/home/jovyan/model
ENV RESULTS_DIR=/home/jovyan/results
ENV RAW_DATA_FILE=TARP.csv


COPY ./Data/Raw_Data/TARP.csv ./raw_data/TARP.csv
COPY ./code/mlops/preprocessing.py ./preprocessing.py
COPY ./code/mlops/train.py ./train.py
COPY ./code/mlops/test.py ./test.py
COPY ./code/mlops/predict.py ./predict.py
COPY ./code/mlops/run_commands.sh ./run_commands.sh

RUN chmod +x run_commands.sh

ENTRYPOINT ["bash", "run_commands.sh"]




                           