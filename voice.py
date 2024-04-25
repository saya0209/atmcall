import pandas as pd
import pickle
import re
import whisper
from konlpy.tag import Kkma
from tqdm import tqdm
from sklearn.feature_extraction.text import TfidfVectorizer

def transcribe(audio):
    model = whisper.load_model("base")
    # load audio and pad/trim it to fit 30 seconds
    audio = whisper.load_audio(audio)
    audio = whisper.pad_or_trim(audio)

    # make log-Mel spectrogram and move to the same device as the model
    mel = whisper.log_mel_spectrogram(audio).to(model.device)

    # detect the spoken language
    _, probs = model.detect_language(mel)

    # decode the audio
    options = whisper.DecodingOptions(fp16 = False)
    result = whisper.decode(model, mel, options)
    
    return result.text    

def stt_model(path):
    easy_text = transcribe(path)
    
    return easy_text

def voice_phishing(path):
    # Kkma 형태소 분석기 객체 생성
    kkma = Kkma()
    nouns = kkma.nouns(stt_model(path))

    sent = ' '.join(nouns)
    
    # Remove symbols, single quotes, commas, and square brackets
    sent = re.sub(r'[\'",\[\]]|[^\s\w]', '', sent)

    # Tokenize the text into words and filter out single-character words
    sent = ' '.join([word for word in sent.split() if len(word) > 1])

    # Remove all numbers
    sent = re.sub(r'\d+', '', sent)

    # Load the model
    with open('voice_svc.pkl', 'rb') as f:
        model = pickle.load(f)

    # Load the vectorizer
    with open('vectorizer.pkl', 'rb') as f:
        vectorizer = pickle.load(f)

    # Apply the same preprocessing steps
    sent = re.sub(r'[\'",\[\]]|[^\s\w]', '', sent)
    sent = ' '.join([word for word in sent.split() if len(word) > 1])
    sent = re.sub(r'\d+', '', sent)

    # Transform the preprocessed text using the loaded vectorizer
    X = vectorizer.transform([sent])

    # Predict using the loaded model
    y_pred = model.predict(X)
    
    if y_pred[0] == 1:
        return '보이스피싱'
        
    else:
        return '정상통화'