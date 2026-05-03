import pandas as pd

import json
import os
from joblib import dump
from sklearn.model_selection import StratifiedKFold, cross_val_score
from xgboost import XGBClassifier


# Set path to inputs
PROCESSED_DATA_DIR = os.environ["PROCESSED_DATA_DIR"]
train_data_file = 'train.csv'
train_data_path = os.path.join(PROCESSED_DATA_DIR, train_data_file)

# Read data
df = pd.read_csv(train_data_path, sep=",")

# Split data into features and target variables
X_train = df.drop('Status', axis=1)
y_train = df['Status']

# Model 
Xgboost_model= XGBClassifier(random_state=21,n_estimators=200,max_depth=5,learning_rate=0.2)
Xgboost_model= Xgboost_model.fit(X_train,y_train)

#cross validation
cv = StratifiedKFold(n_splits=3) 
val_model = cross_val_score(Xgboost_model, X_train, y_train, cv=cv).mean()

# Validation accuracy to JSON
train_metadata = {
    'validation_acc': val_model
}

# Set path to output (model)
MODEL_DIR = os.environ["MODEL_DIR"]
model_name = 'Xgboost_model.joblib'
model_path = os.path.join(MODEL_DIR, model_name)

# Serialize and save model
dump(Xgboost_model, model_path)


# Set path to output (metadata)
RESULTS_DIR = os.environ["RESULTS_DIR"]
train_results_file = 'train_metadata.json'
results_path = os.path.join(RESULTS_DIR, train_results_file)

# Serialize and save metadata
with open(results_path, 'w') as outfile:
    json.dump(train_metadata, outfile)