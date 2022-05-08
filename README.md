# Reproduce `Evaluating shallow and deep learning strategies for the 2018 n2c2 shared task on clinical text classification`

## Reference

The original [JAMIA paper](https://academic.oup.com/jamia/advance-article/doi/10.1093/jamia/ocz149/5568257):

```
@article{oleynik2019evaluating,
  title={Evaluating shallow and deep learning strategies for the 2018 n2c2 shared-task on clinical text classification},
  author={Michel Oleynik and Amila Kugic and Zdenko Kasáč and Markus Kreuzthaler},
  journal={Journal of the American Medical Informatics Association},
  publisher={Oxford University Press},
  year={2019}
}
```

The original paper's repo: https://github.com/bst-mug/n2c2

## Code Dependencies
- JDK8+
- python3 (to run official evaluation scripts)
- make (to compile fastText)
- g++ (to compile fastText)

## Data download instruction
- Download the annotated training and testing data from https://portal.dbmi.hms.harvard.edu/projects/n2c2-nlp/ (registration and data access application is needed)
- Copy all the train xml files (202 in total) to `data/train` directory, copy all test xml files (86 in total) to `data/test` folder

## Setup and Preprocessing
This reproduction work is done on Windows, however using Linux and MacOS will be easier (more convenient to build fastText).
- To build fastText we need to install make
  - (Linux): `sudo apt install make`
  - (Windows): install Chocolately and then `choco install make`
- To build fastText we also need to install g++
  - (Linux): `sudo apt install g++`
  - (Windows): install MinGW-w64
- The `pom.xml` will download v0.2.0 of fastText, and we need to update the `Makefile` to use g++, and finally run `make` to get fastText executable
- Generate self-trained embeddings
  - run `SentenceDumper.java` to get all the sentences
  - run `train_embeddings.sh` to generate the skipgram model if on Linux or directly run corresponding fasttext.exe if on Windows
  - run `VocabularyDumper.java` to get all the words
  - run `print_vectors.sh` to get the word embeddings if on Linux or directly run corresponding fasttext.exe if on Windows
  - rename the generated `vectors.vec` to `self-trained-vectors.vec` and move it to `src/main/java/resources`

## Generate pretrained Embeddings
This section is recommended to perform on a 128 GB memory Linux machine (from one of the cloud providers, e.g. `E16-8ds v5` on Azure)
- `wget https://github.com/facebookresearch/fastText/archive/v0.2.0.zip`
- `unzip v0.2.0.zip`
- `cd fastText-0.2.0 && make`
- `wget https://ftp.ncbi.nlm.nih.gov/pub/lu/Suppl/BioSentVec/BioWordVec_PubMed_MIMICIII_d200.bin`
- copy `vocab.txt` generated from `VocabularyDumper.java` to the VM
- run `print_vectors.sh` using the `BioWordVec_PubMed_MIMICIII_d200.bin` model
- rename the generated `vectors.vec` to `BioWordVec-vectors.vec` and download it to `src/main/java/resources`
- the VM can be stopped now

## Training
- `ClassifierRunner.java` is the entry point for the whole pipeline
- `MajorityClassifier.java` is the baseline classifier
- `RuleBasedClassifier.java` is the rule-based classifier
- `SVMClassifier.java` is the SVM classifier
- `PerceptronClassifier.java` is the logistic regression classifier (the `preTrained` property controls which word embedding to use)
- `LSTMSelfTrainedEmbeddingsClassifier.java` is the LSTM classifier using self-trained word embedding
- `LSTMPreTrainedEmbeddingsClassifier.java` is the LSTM classifier using pretrained BioWordVec embedding

## Evaluation
- `OfficialEvaluator.java` computes recall and F1 score
- `BasicEvaluator.java` computes accuracy
- These metrics are saved as csv files to the `stats` folder

## Results
Accuracy

| Criterion       | Baseline | Rule-Based | SVM    | SELF-LR | PRE-LR | SELF-LSTM | PRE-LSTM |
|-----------------|----------|------------|--------|---------|--------|-----------|----------|
| Abdominal       | 0.6512   | 0.8837     | 0.6512 | 0.6744  | 0.6628 | 0.6512    | 0.6512   |
| Advanced-cad    | 0.5233   | 0.7907     | 0.7326 | 0.6977  | 0.6977 | 0.5116    | 0.5349   |
 | Alcohol-abuse   | 0.9651   | 0.9535     | 0.9651 | 0.9651  | 0.9651 | 0.9651    | 0.9651   |
| Asp-for-mi      | 0.7907   | 0.8605     | 0.7558 | 0.7791  | 0.7791 | 0.6512    | 0.6279   |
| Creatinine      | 0.7209   | 0.8372     | 0.7209 | 0.7558  | 0.7907 | 0.7442    | 0.5581   |
| Dietsupp-2mos   | 0.5116   | 0.9186     | 0.5814 | 0.6279  | 0.6279 | 0.5116    | 0.5349   |
| Drug-abuse      | 0.9651   | 0.9651     | 0.9651 | 0.9651  | 0.9535 | 0.9651    | 0.9651   |
| English         | 0.8488   | 0.9419     | 0.8488 | 0.8488  | 0.8488 | 0.8488    | 0.8256   |
| Hba1c           | 0.593    | 0.9419     | 0.6512 | 0.5814  | 0.6047 | 0.5581    | 0.5814   |
| Keto-1yr        | 1        | 1          | 1      | 1       | 1      | 1         | 1        |
| Major-diabetes  | 0.5      | 0.8372     | 0.7558 | 0.7442  | 0.7442 | 0.5233    | 0.5581   |
| Makes-decisions | 0.9651   | 0.9651     | 0.9651 | 0.9651  | 0.9651 | 0.9651    | 0.9651   |
| Mi-6mos         | 0.907    | 0.9651     | 0.9302 | 0.907   | 0.907  | 0.907     | 0.907    |
| Overall         | 0.7648   | 0.9123     | 0.8095 | 0.8086  | 0.8113 | 0.754     | 0.7442   |

Overall F1 score

| Criterion       | Baseline | Rule-Based | SVM    | SELF-LR | PRE-LR | SELF-LSTM | PRE-LSTM |
|-----------------|----------|------------|--------|---------|--------|-----------|----------|
| Abdominal       | 0.3944   | 0.872      | 0.6028 | 0.6221  | 0.5959 | 0.5521    | 0.5499   |
| Advanced-cad    | 0.3435   | 0.7902     | 0.7281 | 0.6868  | 0.6838 | 0.5039    | 0.5812   |
| Alcohol-abuse   | 0.4911   | 0.4881     | 0.4911 | 0.4911  | 0.4911 | 0.4911    | 0.4911   |
| Asp-for-mi      | 0.4416   | 0.7095     | 0.6063 | 0.606   | 0.606  | 0.5081    | 0.5082   |
| Creatinine      | 0.4189   | 0.8071     | 0.6532 | 0.7073  | 0.7399 | 0.559     | 0.4774   |
| Dietsupp-2mos   | 0.3385   | 0.9185     | 0.5814 | 0.6261  | 0.6261 | 0.5201    | 0.5415   |
| Drug-abuse      | 0.4911   | 0.691      | 0.4911 | 0.4911  | 0.4881 | 0.4911    | 0.4911   |
| English         | 0.4591   | 0.8644     | 0.4591 | 0.4591  | 0.4591 | 0.4522    | 0.4557   |
| Hba1c           | 0.3723   | 0.9382     | 0.6267 | 0.5393  | 0.577  | 0.5318    | 0.5318   |
| Keto-1yr        | 0.5      | 0.5        | 0.5    | 0.5     | 0.5    | 0.5       | 0.5      |
| Major-diabetes  | 0.3333   | 0.8369     | 0.7555 | 0.7391  | 0.742  | 0.468     | 0.4555   |
| Makes-decisions | 0.4911   | 0.4911     | 0.4911 | 0.4911  | 0.4911 | 0.4911    | 0.4911   |
| Mi-6mos         | 0.4756   | 0.8752     | 0.6815 | 0.4756  | 0.4756 | 0.4756    | 0.4756   |
| Overall-Micro   | 0.7608   | 0.91       | 0.8035 | 0.8037  | 0.8063 | 0.7392    | 0.733    |
| Overall-Macro   | 0.427    | 0.7525     | 0.5899 | 0.5719  | 0.5751 | 0.5034    | 0.5038   |