## helm setup
```
$ kubectl apply -f https://raw.githubusercontent.com/IBM-Cloud/kube-samples/master/rbac/serviceaccount-tiller.yaml
$ helm init --service-account tiller

$ cd ./remind
$ kubectl create namespace kerfume-reminder
$ helm upgrade --atomic --wait --install kreminder --namespace kerfume-reminder .
```
