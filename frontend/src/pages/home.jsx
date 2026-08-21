import { SiteNav } from '@/components/site/site-nav'
import { Hero } from '@/components/site/hero'
import { Features } from '@/components/site/features'
import { Workflow } from '@/components/site/workflow'
import { Faq } from '@/components/site/faq'
import { SiteFooter } from '@/components/site/site-footer'

export default function HomePage() {
  return (
    <>
      <SiteNav />
      <main>
        <Hero />
        <Features />
        <Workflow />
        <Faq />
      </main>
      <SiteFooter />
    </>
  )
}
