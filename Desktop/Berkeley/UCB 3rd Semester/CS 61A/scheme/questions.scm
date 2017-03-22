(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cdar x) (cdr (car x)))
(define (cddr x) (cdr (cdr x)))

; Some utility functions that you may find useful to implement.
(define (map proc items)
  (if (null? items) 
    nil
    (cons (proc (car items)) (map proc (cdr items)))))

    

(define (cons-all first rests)
  (define (proc lists)
    (cons first lists))
  (map proc rests))



(define (zip pairs)
  (define (removal lists)
    (if (eq? lists nil)
      (define lists nil)
      (define lists (cdr lists)))
    lists)

  (define (firsts pairs)
    (if (null? pairs)
      nil
      (cons (caar pairs) (firsts (cdr pairs)))))

  (if (eq? pairs nil)
    '(() ())
    (if (or (null? pairs) (null? (car pairs)))
      nil
      (cons (firsts pairs) (zip (map removal pairs)))
      )
    )
  )
  
  
;; Problem 17
;; Returns a list of two-element lists
(define (enumerate s)
  ; BEGIN PROBLEM 17
  (define n 0)
  (define (enumerates s n)
    (cond
      ((null? s) s)
      (else (cons (cons n (cons (car s) nil)) (enumerates (cdr s) (+ n 1))))
      )
    )
  (enumerates s n)
  )
  ; END PROBLEM 17

;; Problem 18
;; List all ways to make change for TOTAL with DENOMS
(define (list-change total denoms)
  ; BEGIN PROBLEM 18
  (cond
    ((null? denoms) nil)
    ((= total 0) (list nil))
    ((> (car denoms) total) (list-change total (cdr denoms)))
    (else (append (cons-all (car denoms) (list-change (- total (car denoms)) denoms))
                    (list-change total (cdr denoms))))
  )
  )
  ; END PROBLEM 18

;; Problem 19
;; Returns a function that checks if an expression is the special form FORM
(define (check-special form)
  (lambda (expr) (equal? form (car expr))))

(define lambda? (check-special 'lambda))
(define define? (check-special 'define))
(define quoted? (check-special 'quote))
(define let?    (check-special 'let))

;; Converts all let special forms in EXPR into equivalent forms using lambda
(define (let-to-lambda expr)
  (cond ((atom? expr)
         ; BEGIN PROBLEM 19
         expr
         ; END PROBLEM 19
         )
        ((quoted? expr)
         ; BEGIN PROBLEM 19
         expr
         ; END PROBLEM 19
         )
        ((or (lambda? expr)
             (define? expr))
         (let ((form   (car expr))
               (params (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 19
           (cons form
            (cons params
              (map let-to-lambda body)))
           ; END PROBLEM 19
           ))
        ((let? expr)
         (let ((values (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 19
           (cons 
            (cons 'lambda 
              (cons (car (zip values)) 
                (map let-to-lambda body))) 
            (car (map let-to-lambda (cdr (zip values)))))
           ))
        (else
         ; BEGIN PROBLEM 19
         (map let-to-lambda expr)
         ; END PROBLEM 19
         )))
