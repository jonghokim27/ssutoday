FROM python:3.11

WORKDIR /usr/src/app
ENV TZ Asia/Seoul

COPY requirements.txt /usr/src/app/
RUN pip install --no-cache-dir -r requirements.txt


ENTRYPOINT ["python","main.py"]