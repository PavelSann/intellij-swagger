package org.zalando.intellij.swagger.ui.provider;

import java.util.Optional;

import com.intellij.mock.MockApplication;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.zalando.intellij.swagger.index.IndexFacade;
import org.zalando.intellij.swagger.service.PsiFileService;
import org.zalando.intellij.swagger.service.SwaggerFileService;

public class FileDocumentListenerTest {

  private IndexFacade fakeIndexFacade = mock(IndexFacade.class);

  private SwaggerFileService fakeSwaggerFileService = mock(SwaggerFileService.class);

  private PsiFileService fakePsiFileService = mock(PsiFileService.class);

  private Document fakeDocument = mock(Document.class);

  private final FileDocumentListener listener = new FileDocumentListener();

  @Before
  public void setUp() {
    MockApplication app = MockApplication.setUp(() -> {});
    app.registerService(IndexFacade.class, fakeIndexFacade);
    app.registerService(PsiFileService.class, fakePsiFileService);
    app.registerService(SwaggerFileService.class, fakeSwaggerFileService);
  }

  @Test
  public void thatSwaggerUiConversionIsCalled() {
    final PsiFile fakePsiFile = mock(PsiFile.class);
    final Project fakeProject = mock(Project.class);
    final VirtualFile fakeSpecFile = mock(VirtualFile.class);

    when(fakePsiFile.getProject()).thenReturn(fakeProject);
    when(fakePsiFileService.fromDocument(fakeDocument)).thenReturn(Optional.of(fakePsiFile));
    when(fakeIndexFacade.isIndexReady(fakeProject)).thenReturn(true);
    when(fakeIndexFacade.getMainSpecFile(fakePsiFile)).thenReturn(Optional.of(fakeSpecFile));

    listener.beforeDocumentSaving(fakeDocument);

    verify(fakeSwaggerFileService, times(1)).convertSwaggerToHtmlAsync(fakeSpecFile);
  }

  @Test
  public void thatSwaggerUiConversionIsNotCalledIfIndexIsNotReady() {
    final PsiFile fakePsiFile = mock(PsiFile.class);
    final Project fakeProject = mock(Project.class);

    when(fakePsiFile.getProject()).thenReturn(fakeProject);
    when(fakePsiFileService.fromDocument(fakeDocument)).thenReturn(Optional.of(fakePsiFile));
    when(fakeIndexFacade.isIndexReady(fakeProject)).thenReturn(false);

    listener.beforeDocumentSaving(fakeDocument);

    verify(fakeSwaggerFileService, never()).convertSwaggerToHtmlAsync(any());
  }

  @Test
  public void thatSwaggerUiConversionIsNotCalledIfSpecFileDoesNotExist() {
    final PsiFile fakePsiFile = mock(PsiFile.class);
    final Project fakeProject = mock(Project.class);

    when(fakePsiFile.getProject()).thenReturn(fakeProject);
    when(fakePsiFileService.fromDocument(fakeDocument)).thenReturn(Optional.of(fakePsiFile));
    when(fakeIndexFacade.isIndexReady(fakeProject)).thenReturn(true);
    when(fakeIndexFacade.getMainSpecFile(fakePsiFile)).thenReturn(Optional.empty());

    listener.beforeDocumentSaving(fakeDocument);

    verify(fakeSwaggerFileService, never()).convertSwaggerToHtmlAsync(any());
  }

  @Test
  public void thatSwaggerUiConversionIsNotIsPsiFileDoesNotExist() {
    when(fakePsiFileService.fromDocument(fakeDocument)).thenReturn(Optional.empty());

    listener.beforeDocumentSaving(fakeDocument);

    verify(fakeSwaggerFileService, never()).convertSwaggerToHtmlAsync(any());
  }
}
