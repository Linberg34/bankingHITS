import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { MonitoringFeedService } from './data/mock-monitoring-feed.service';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        {
          provide: MonitoringFeedService,
          useValue: {
            logs: signal([]),
            paused: signal(false),
            togglePause: jest.fn(),
            clear: jest.fn(),
            burst: jest.fn(),
          },
        },
      ],
    }).compileComponents();
  });

  it('should render dashboard title', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Интерфейс мониторинга');
  });
});
