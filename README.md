# MLND Deep Learning Capstone
Using deep learning and tensorflow to detect Street View House Number

## Source Code
I tried to make the source code namings to be as clear as possible, but here are the files that make up this project
- `01-Download and Extraction.ipynb`
- `02-Exploration.ipynb`
- `03-Data Preparation.ipynb`
- `04-Model Training.ipynb`
- `05-Prediction.ipynb`

## Requirements

This project requires **Python 2.7** and the following Python libraries installed:

- [TensorFlow](http://www.tensorflow.org/)
- [NumPy](http://www.numpy.org/)
- [matplotlib](http://matplotlib.org/)
- [scikit-learn](http://scikit-learn.org/stable/)
- [SciPy library](http://www.scipy.org/scipylib/index.html)
- [Six](http://pypi.python.org/pypi/six/)
- [h5py](http://pypi.python.org/pypi/h5py/)
- [Pillow](http://pypi.python.org/pypi/Pillow/)

It also requires a fast computer, 4 cores CPU or more, 8Gb RAM or more. I trained using cpu and did not setup the project to support GPU

You will also need to have software installed to run and execute an [iPython Notebook](http://ipython.org/notebook.html)

## Run

Open `Ipython Notebook` in the root folder. I learnt that command would be changed to `Jupyter Notebook`

Open the 5 Files and run their contents one after the other. Some commands take longer than others


## Data

This project uses the [The Street View House Numbers (SVHN) Dataset](http://ufldl.stanford.edu/housenumbers/).

SVHN is a real-world image dataset for developing machine learning and object recognition algorithms with minimal requirement on data preprocessing and formatting. It can be seen as similar in flavor to MNIST (e.g., the images are of small cropped digits), but incorporates an order of magnitude more labeled data (over 600,000 digit images) and comes from a significantly harder, unsolved, real world problem (recognizing digits and numbers in natural scene images). SVHN is obtained from house numbers in Google Street View images. 