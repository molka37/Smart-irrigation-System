import os
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.preprocessing import LabelBinarizer
from joblib import dump


# Set path for the input
RAW_DATA_DIR = os.environ["RAW_DATA_DIR"]
RAW_DATA_FILE = os.environ["RAW_DATA_FILE"]
raw_data_path = os.path.join(RAW_DATA_DIR, RAW_DATA_FILE)

# Read dataset
df = pd.read_csv(raw_data_path, sep=",")

# Drop irrelevant features 
L = ['Time','Wind gust (Km/h)','Wind speed (Km/h)', 'Pressure (KPa)','ph','rainfall','N','P','K','Air temperature (C)','Air humidity (%)']
df.drop(columns=L,axis=1,inplace=True)

#Set 'Status variable to binary
encoder = LabelBinarizer()
df['Status'] = encoder.fit_transform(df['Status'])

#Data vizualisation
plt.figure(figsize=(8, 5))
sns.heatmap(data=df.corr(), annot=True, vmin=-1, vmax=1)

ax = sns.countplot(x="Status",width= 0.2,data=df)
plt.show()

sns.displot(df, x="Temperature", binwidth=3, bins=20, discrete=True, height=5)
sns.displot(df, x="Soil Moisture", binwidth=3, bins=20, discrete=True, height=5)
plt.show()

#Split features and Target variable
X=df.drop(columns='Status',axis=1)
y=df['Status'].to_frame()

#Scaling data
scaler = StandardScaler()
X = scaler.fit_transform(X)
X=pd.DataFrame(X)

data=pd.concat([X,y],axis=1)
#Split train and test sets
train, test = train_test_split(data, test_size=0.3, stratify=data['Status'])


# Set path to the outputs
MODEL_DIR = os.environ["MODEL_DIR"]
PROCESSED_DATA_DIR = os.environ["PROCESSED_DATA_DIR"]
train_path = os.path.join(PROCESSED_DATA_DIR, 'train.csv')
test_path = os.path.join(PROCESSED_DATA_DIR, 'test.csv')
scaler_path = os.path.join(MODEL_DIR, 'scaler.joblib')

dump(scaler, scaler_path)

# Save csv
train.to_csv(train_path, index=False)
test.to_csv(test_path,  index=False)
