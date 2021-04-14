package org.zalando.intellij.swagger;

import com.intellij.mock.MockApplication;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.zalando.intellij.swagger.index.IndexFacade;

public class SwaggerJsonCatalogExclusionTest {

  private IndexFacade fakeIndexFacade = mock(IndexFacade.class);
  private ProjectManager fakeProjectManager = mock(ProjectManager.class);

  private final SwaggerJsonCatalogExclusion exclusion = new SwaggerJsonCatalogExclusion();

  @Before
  public void setUp() {
    MockApplication app = MockApplication.setUp(() -> {});
    app.registerService(IndexFacade.class, fakeIndexFacade);
    app.registerService(ProjectManager.class, fakeProjectManager);
  }

  @Test
  public void thatIndexIsNotUsedIfItIsNotReady() {
    final VirtualFile fakeVirtualFile = mock(VirtualFile.class);
    final Project fakeProject = mock(Project.class);

    when(fakeProjectManager.getOpenProjects()).thenReturn(new Project[] {fakeProject});
    when(fakeIndexFacade.isIndexReady(fakeProject)).thenReturn(false);

    exclusion.isExcluded(fakeVirtualFile);

    verify(fakeIndexFacade, never()).isMainSpecFile(any(), any());
  }

  @Test
  public void thatFileIsNotExcludedIfIndexIsNotReady() {
    final VirtualFile fakeVirtualFile = mock(VirtualFile.class);
    final Project fakeProject = mock(Project.class);

    when(fakeProjectManager.getOpenProjects()).thenReturn(new Project[] {fakeProject});
    when(fakeIndexFacade.isIndexReady(fakeProject)).thenReturn(false);

    final boolean result = exclusion.isExcluded(fakeVirtualFile);

    assertFalse(result);
  }

  @Test
  public void thatFileIsNotExcludedIfThereAreNoOpenProjects() {
    final VirtualFile fakeVirtualFile = mock(VirtualFile.class);
    final Project fakeProject = mock(Project.class);

    when(fakeProjectManager.getOpenProjects()).thenReturn(new Project[0]);
    when(fakeIndexFacade.isIndexReady(fakeProject)).thenReturn(true);

    final boolean result = exclusion.isExcluded(fakeVirtualFile);

    assertFalse(result);
  }

  @Test
  public void thatSpecFileIsExcluded() {
    final VirtualFile fakeVirtualFile = mock(VirtualFile.class);
    final Project fakeProject = mock(Project.class);

    when(fakeProjectManager.getOpenProjects()).thenReturn(new Project[] {fakeProject});
    when(fakeIndexFacade.isIndexReady(fakeProject)).thenReturn(true);
    when(fakeIndexFacade.isMainSpecFile(fakeVirtualFile, fakeProject)).thenReturn(true);

    final boolean result = exclusion.isExcluded(fakeVirtualFile);

    assertTrue(result);
  }
}
