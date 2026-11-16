# Kind + Helm Prototype

Minimal Anleitung, um den Self-Healing Orchestrierungsprototypen lokal in einem Kind-Cluster zu starten.

## Voraussetzungen
- Docker und Kind
- Helm 3
- Optional: `kubectl port-forward`/`kubectl logs`

## Cluster erstellen
```bash
kind create cluster --name aicurator --config - <<'KCFG'
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
  - role: worker
KCFG
```

## Images bauen und in Kind laden
Aus dem Repo-Root:
```bash
# Backend + Frontend (existierende Dockerfiles)
docker build -t taxishare-backend:latest app/backend
npm --prefix app/frontend install && npm --prefix app/frontend run build
docker build -t taxishare-frontend:latest app/frontend

# Agent Gateway
docker build -t agent-gateway:latest services/agent-gateway

# Test-Runner (nutzt Integrationstests)
docker build -t self-healing-tests:latest integrationtest/self-healing-integration-tests

# In den Kind-Cluster laden
kind load docker-image taxishare-backend:latest --name aicurator
kind load docker-image taxishare-frontend:latest --name aicurator
kind load docker-image agent-gateway:latest --name aicurator
kind load docker-image self-healing-tests:latest --name aicurator
```

## Helm Chart installieren
```bash
helm upgrade --install aicurator deploy/helm/aicurator-prototype \
  --namespace aicurator --create-namespace \
  --set openai.apiKey="$OPENAI_KEY" --set openai.mockEnabled=${OPENAI_KEY:+false} \
  --set agentGateway.image=agent-gateway:latest \
  --set backend.image=taxishare-backend:latest \
  --set frontend.image=taxishare-frontend:latest \
  --set runner.image=self-healing-tests:latest
```

## n8n Workflow importieren
- Port-Forward n8n UI:
```bash
kubectl -n aicurator port-forward svc/n8n 5678:5678
```
- UI öffnen: http://localhost:5678/
- Workflow aus `n8n/workflows/selfhealing-prototype.json` importieren.

## Ausführen
Webhook triggern:
```bash
curl -X POST http://localhost:5678/webhook/run-selfhealing \
  -H 'Content-Type: application/json' \
  -d '{"conversationId":"demo-123"}'
```

Beispielantwort:
```json
{
  "status": "PASS",
  "healingApplied": true,
  "durationMs": 12345,
  "failedSelector": "#old-login",
  "healedSelector": "[data-testid=login-button]",
  "artifacts": {"junitXml": "(siehe Job Log)"}
}
```

## Debuggen
```bash
kubectl -n aicurator get pods
kubectl -n aicurator logs deployment/agent-gateway
kubectl -n aicurator logs job/test-runner
kubectl -n aicurator port-forward svc/frontend 8088:80
```

## UI Änderung für Healing simulieren
Der React-Frontend-Build enthält beide Versionen. Setze ENV `UI_VARIANT=v2` im Frontend-Deployment (Helm Value `frontend.uiVariant`) oder ändere den Service, um absichtlich einen gebrochenen Locator zu erzeugen. Die Tests erkennen den Bruch und rufen den `/mcp`-Endpoint mit `heal_locator` auf.
