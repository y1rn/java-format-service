package y1rn.javaformat;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.eclipse.lsp4j.jsonrpc.StandardLauncher;
import org.eclipse.lsp4j.jsonrpc.json.JsonRpcMethod;
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler;
import org.eclipse.lsp4j.jsonrpc.json.StreamMessageProducer;
import org.eclipse.lsp4j.jsonrpc.messages.Message;
import org.eclipse.lsp4j.jsonrpc.messages.MessageIssue;
import org.eclipse.lsp4j.jsonrpc.messages.RequestMessage;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseMessage;

@Log
public class RPC {

  public static void main(String[] args) throws ClassNotFoundException {
    // logger config
    Class.forName("y1rn.javaformat.LogConfig");
    ExecutorService es = Executors.newCachedThreadPool();
    Map<String, JsonRpcMethod> mm =
        Map.of(
            FormatHandler.METHOD_FORMAT,
                JsonRpcMethod.request(FormatHandler.METHOD_FORMAT, List.class, Request.class),
            FormatHandler.METHOD_EXIT, JsonRpcMethod.notification(FormatHandler.METHOD_EXIT));
    MessageJsonHandler mjh = new MessageJsonHandler(mm);

    FormatHandler fh = new FormatHandler(System.out, mjh);

    StreamMessageProducer smp =
        new StreamMessageProducer(
            System.in,
            mjh,
            (Message message, List<MessageIssue> issues) -> {
              log.info(
                  () -> {
                    if (log.isLoggable(Level.INFO)) {
                      return message.toString();
                    }
                    return null;
                  });
              ResponseMessage resp = new ResponseMessage();
              resp.setId(Integer.parseInt(((RequestMessage) message).getId()));

              if (issues != null && !issues.isEmpty()) {
                var issue = issues.get(0);
                ResponseError re =
                    new ResponseError(issue.getIssueCode(), issue.getText(), issue.getCause());
                resp.setError(re);
                if (issue.getCause() != null) {
                  log.log(Level.SEVERE, issue.getCause().getMessage(), issue.getCause());
                }
                log.log(Level.SEVERE, issue.getText());
              } else {
                resp.setResult(Collections.emptyList());
              }

              fh.writeResponse(resp);
            });
    StandardLauncher<Void> launcher = new StandardLauncher<>(smp, fh, es, null, null);
    launcher.startListening();
  }
}
