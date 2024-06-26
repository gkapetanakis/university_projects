mod accounts_catalog;

pub use accounts_catalog::AccountsCatalog;

#[derive(Debug)]
pub enum AccountError {
    InsufficientFunds(u32),
}

const BUF_LEN: usize = 32;

// A NoncePool is a circular buffer that keeps track of the nonces used by an account.
// It only accurately keeps track of the last BUF_LEN nonces used by the account.
// Every nonce before that is considered used by default.
// Every nonce after that is considered unused by default.

/*
    * Why use a circular buffer instead of a single integer?

    Suppose node0 and node1 are the only two nodes in the network.
    node0 validates a block containing [ n1-nonce0, n1-nonce1, n1-nonce3 ] because
    due a network error, it didn't receive n1-nonce2.
    On receiving the block, node1 will mark n1-nonce2 as invalid, since it will be
    a lower number than the next nonce it expects.
    This does not violate the correctness of the system, but it is annoying for the users.
    Using a circular buffer avoids that for up to BUF_LEN nonces.

    * Why use a circular buffer instead of a hash set?

    Less and completely prectable memory usage. It's very unlikely that a similar scenario
    to the one described above will happen with a nonce difference of more than BUF_LEN.
    So, using a hash set practically provides little to no benefit over a circular buffer.
*/

#[derive(Debug, Default, Clone)]
pub struct NoncePool {
    iter: usize,
    buf_end: usize,
    buf: [bool; BUF_LEN],
}

impl NoncePool {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn next(&self) -> u64 {
        (self.iter * BUF_LEN + self.buf_end) as u64
    }

    pub fn mark_used(&mut self, nonce: u64) {
        let nonce_iter = (nonce / BUF_LEN as u64) as usize;
        let nonce_index = (nonce % BUF_LEN as u64) as usize;

        // nonce_iter < self.iter - 1
        if self.iter.checked_sub(1).is_some_and(|i| nonce_iter < i) {
            // in the buffer, only numbers from the current
            // and the immediately previous iteration can coexist
            // all previous numbers are considered marked by default
            return;
        }

        // nonce_iter == self.iter - 1
        if self.iter.checked_sub(1).is_some_and(|i| nonce_iter == i) {
            // if the number belongs to the previous iteration
            // but its index has been overwritten, it is considered
            // used by default
            if nonce_index < self.buf_end {
                return;
            }
            // otherwise mark it as used
            else {
                self.buf[nonce_index] = true;
                return;
            }
        }

        if nonce_iter == self.iter {
            // if the number belongs to the current iteration
            // and its index is in the valid range,
            // mark it as used
            if nonce_index < self.buf_end {
                self.buf[nonce_index] = true;
            }
            // otherwise extend the buffer up to it
            // and reset the inbetween places to false
            // then, mark it as used
            else {
                for i in self.buf_end..nonce_index {
                    self.buf[i] = false
                }

                self.buf[nonce_index] = true;
                self.buf_end = (nonce_index + 1) % BUF_LEN;
                if self.buf_end == 0 {
                    self.iter += 1;
                }

                return;
            }
        }

        if nonce_iter == self.iter + 1 {
            for i in self.buf_end..BUF_LEN {
                self.buf[i] = false;
            }

            for i in 0..nonce_index {
                self.buf[i] = false;
            }

            self.iter = nonce_iter;
            self.buf[nonce_index] = true;
            self.buf_end = (nonce_index + 1) % BUF_LEN;
            if self.buf_end == 0 {
                self.iter += 1;
            }

            return;
        }

        if nonce_iter > self.iter + 1 {
            self.iter = nonce_iter;
            self.buf = [false; BUF_LEN];
            self.buf[nonce_index] = true;
            self.buf_end = (nonce_index + 1) % BUF_LEN;
            if self.buf_end == 0 {
                self.iter += 1;
            }
        }
    }

    pub fn is_marked_used(&self, nonce: u64) -> bool {
        let nonce_iter = (nonce / BUF_LEN as u64) as usize;
        let nonce_index = (nonce % BUF_LEN as u64) as usize;

        if self.iter.checked_sub(1).is_some_and(|i| nonce_iter < i) {
            return true;
        }

        if self.iter.checked_sub(1).is_some_and(|i| nonce_iter == i) {
            return nonce_index < self.buf_end || self.buf[nonce_index];
        }

        if nonce_iter == self.iter {
            return nonce_index < self.buf_end && self.buf[nonce_index];
        }

        false
    }
}

// An Account is a struct that represents a user account in the system.
// It keeps track of the account's ID, nonce pool, held cents, and staked cents.
// Why do accounts have ID? In order to facilitate automated testing (check out bin/helper.rs)

#[derive(Debug, Clone)]
pub struct Account {
    id: u32,
    nonce_pool: NoncePool,
    held_cents: u32,
    staked_cents: u32,
}

impl Account {
    pub fn add_held(&mut self, amnt: u32) {
        self.held_cents += amnt;
    }

    pub fn sub_held(&mut self, amnt: u32) -> Result<(), AccountError> {
        if amnt > self.held_cents {
            return Err(AccountError::InsufficientFunds(amnt - self.held_cents));
        }

        self.held_cents -= amnt;

        Ok(())
    }

    pub fn add_staked(&mut self, amnt: u32) {
        self.staked_cents += amnt;
    }

    pub fn sub_staked(&mut self, amnt: u32) -> Result<(), AccountError> {
        if amnt > self.staked_cents {
            return Err(AccountError::InsufficientFunds(amnt - self.staked_cents));
        }

        self.staked_cents -= amnt;

        Ok(())
    }

    pub fn id(&self) -> u32 {
        self.id
    }

    pub fn nonce_pool(&self) -> &NoncePool {
        &self.nonce_pool
    }

    pub fn nonce_pool_mut(&mut self) -> &mut NoncePool {
        &mut self.nonce_pool
    }

    pub fn held_cents(&self) -> u32 {
        self.held_cents
    }

    pub fn staked_cents(&self) -> u32 {
        self.staked_cents
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_nonce_impl() {
        let mut pool = NoncePool::new();
        assert_eq!(pool.next(), 0);
        assert!(!pool.is_marked_used(0));
        assert!(!pool.is_marked_used(1));
        assert!(!pool.is_marked_used(2));
        assert!(!pool.is_marked_used(3));
        assert!(!pool.is_marked_used(BUF_LEN as u64 - 1));
        assert!(!pool.is_marked_used(BUF_LEN as u64));
        assert!(!pool.is_marked_used(2 * BUF_LEN as u64));

        pool.mark_used(0);
        assert_eq!(pool.next(), 1);
        assert!(pool.is_marked_used(0));
        assert!(!pool.is_marked_used(1));
        assert!(!pool.is_marked_used(2));
        assert!(!pool.is_marked_used(3));
        assert!(!pool.is_marked_used(BUF_LEN as u64 - 1));
        assert!(!pool.is_marked_used(BUF_LEN as u64));
        assert!(!pool.is_marked_used(BUF_LEN as u64 + 1));
        assert!(!pool.is_marked_used(2 * BUF_LEN as u64));

        pool.mark_used(2);
        assert_eq!(pool.next(), 3);
        assert!(pool.is_marked_used(0));
        assert!(!pool.is_marked_used(1));
        assert!(pool.is_marked_used(2));
        assert!(!pool.is_marked_used(3));
        assert!(!pool.is_marked_used(BUF_LEN as u64 - 1));
        assert!(!pool.is_marked_used(BUF_LEN as u64));
        assert!(!pool.is_marked_used(BUF_LEN as u64 + 1));
        assert!(!pool.is_marked_used(2 * BUF_LEN as u64));

        pool.mark_used(BUF_LEN as u64);
        assert_eq!(pool.next(), BUF_LEN as u64 + 1);
        assert!(pool.is_marked_used(0));
        assert!(!pool.is_marked_used(1));
        assert!(pool.is_marked_used(2));
        assert!(!pool.is_marked_used(3));
        assert!(!pool.is_marked_used(BUF_LEN as u64 - 1));
        assert!(pool.is_marked_used(BUF_LEN as u64));
        assert!(!pool.is_marked_used(BUF_LEN as u64 + 1));
        assert!(!pool.is_marked_used(2 * BUF_LEN as u64));

        pool.mark_used(BUF_LEN as u64 + 1);
        assert_eq!(pool.next(), BUF_LEN as u64 + 2);
        assert!(pool.is_marked_used(0));
        assert!(pool.is_marked_used(1));
        assert!(pool.is_marked_used(2));
        assert!(!pool.is_marked_used(3));
        assert!(!pool.is_marked_used(BUF_LEN as u64 - 1));
        assert!(pool.is_marked_used(BUF_LEN as u64));
        assert!(pool.is_marked_used(BUF_LEN as u64 + 1));
        assert!(!pool.is_marked_used(2 * BUF_LEN as u64));

        pool.mark_used(2 * BUF_LEN as u64);
        assert_eq!(pool.next(), (2 * BUF_LEN) as u64 + 1);
        assert!(pool.is_marked_used(0));
        assert!(pool.is_marked_used(1));
        assert!(pool.is_marked_used(2));
        assert!(pool.is_marked_used(3));
        assert!(pool.is_marked_used(BUF_LEN as u64 - 1));
        assert!(pool.is_marked_used(BUF_LEN as u64));
        assert!(pool.is_marked_used(BUF_LEN as u64 + 1));
        assert!(pool.is_marked_used(2 * BUF_LEN as u64));
    }
}
