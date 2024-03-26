{{/*Define the current date as a Helm value using the now function.*/}}
{{- define "mychart.currentDate" -}}
{{- now | date "2006-01-02" -}}
{{- end -}}

{{/*Define the version as a Helm value.*/}}
{{- define "mychart.version" -}}
1.0.0
{{- end -}}