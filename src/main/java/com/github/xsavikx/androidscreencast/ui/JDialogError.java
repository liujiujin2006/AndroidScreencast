package com.github.xsavikx.androidscreencast.ui;

import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class JDialogError extends JDialog {

    private JLabel errorDialogLabel;
    private JScrollPane scrollPane;
    private JTextArea errorDescription;

    public JDialogError(Throwable ex) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents(ex);
    }

    private void initComponents(Throwable ex) {
        errorDialogLabel = new JLabel();
        scrollPane = new JScrollPane();
        errorDescription = new JTextArea();
        errorDescription.setLineWrap(true);
        errorDescription.setWrapStyleWord(true);


        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5, 5));


        errorDialogLabel.setText(ex.getClass().getSimpleName());
        errorDialogLabel.setLabelFor(errorDescription);
        errorDialogLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(errorDialogLabel, BorderLayout.NORTH);
        setErrorDetails(ex);

        scrollPane.setViewportView(errorDescription);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) screenSize.getWidth() >> 1, (int) screenSize.getHeight() >> 1);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
    }

    private void setErrorDetails(Throwable ex) {
        errorDialogLabel.setText(ex.getClass().getSimpleName());
        if (ex.getClass() == RuntimeException.class && ex.getCause() != null)
            ex = ex.getCause();
        try (StringWriter stringWriter = new StringWriter();) {
            AndroidScreenCastRuntimeException realCause = getCause(ex);
            if (realCause != null) {
                errorDialogLabel.setText(realCause.getClass().getSimpleName());
                stringWriter.append(realCause.getMessage()).append('\n').append('\n');
                stringWriter.append(realCause.getAdditionalInformation());
            } else {
                stringWriter.append(ex.getMessage()).append('\n').append('\n');
                ex.printStackTrace(new PrintWriter(stringWriter));
            }
            errorDescription.setText(stringWriter.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AndroidScreenCastRuntimeException getCause(Throwable ex) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof AndroidScreenCastRuntimeException) {
                return (AndroidScreenCastRuntimeException) cause;
            }
            cause = cause.getCause();
        }
        return null;
    }
}
