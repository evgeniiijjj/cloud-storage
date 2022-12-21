FROM centos:7

RUN yum install -y epel-release && yum install -y nginx

RUN yum clean all

COPY nginx.conf /etc/nginx/nginx.conf

RUN echo "daemon off;" >> /etc/nginx/nginx.conf

CMD ["nginx"]

EXPOSE 5500